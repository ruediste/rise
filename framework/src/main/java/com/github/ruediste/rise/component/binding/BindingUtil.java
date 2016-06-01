package com.github.ruediste.rise.component.binding;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.fragment.ValueHandle;
import com.github.ruediste.rise.nonReloadable.lambda.LambdaExpression;
import com.github.ruediste.rise.nonReloadable.lambda.expression.MemberExpression;
import com.github.ruediste.rise.nonReloadable.lambda.expression.MemberExpressionType;
import com.github.ruediste.rise.nonReloadable.lambda.expression.NullExpressionVisitor;
import com.github.ruediste.rise.nonReloadable.lambda.expression.ThisExpression;
import com.github.ruediste.rise.nonReloadable.lambda.expression.UnaryExpression;
import com.github.ruediste.rise.nonReloadable.lambda.expression.UnaryExpressionType;
import com.github.ruediste.rise.util.Try;

@Singleton
public class BindingUtil {

    private static final MemberExpressionExtractor MEMBER_EXPRESSION_EXTRACTOR = new MemberExpressionExtractor();
    private static final Method transformMethod;
    private static final Field controllerField;

    static {
        try {
            transformMethod = BindingTransformer.class.getMethod("transform", Object.class);
            controllerField = ViewComponentBase.class.getDeclaredField("controller");
        } catch (NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> BindingInfo<T> extractBindingInfo(Supplier<T> lambda, ValueHandle<T> viewValueHandle) {
        return tryExtractBindingInfo(lambda).get();
    }

    public static <T> Try<BindingInfo<T>> tryExtractBindingInfo(Supplier<T> lambda) {
        BindingInfo<T> info = new BindingInfo<T>();
        info.lambda = lambda;
        LambdaExpression<Object> exp = LambdaExpression.parse(lambda);
        MemberExpression memberExp = exp.getBody().accept(MEMBER_EXPRESSION_EXTRACTOR);
        // check for a transformer
        if (transformMethod.equals(memberExp.getMember())) {
            info.transformer = (BindingTransformer<?, ?>) exp.withBody(memberExp.getInstance()).compile()
                    .apply(new Object[] {});
            memberExp = memberExp.getArguments().get(0).accept(MEMBER_EXPRESSION_EXTRACTOR);
        }

        // get accessed property
        Optional<PropertyInfo> modelProperty = PropertyUtil.tryGetProperty(memberExp.getMember());
        if (!modelProperty.isPresent()) {
            MemberExpression tmp = memberExp;
            return Try.failure(() -> new RuntimeException("Unable to get property for " + tmp.getMember()));
        }
        info.modelProperty = modelProperty.get();

        // determine writeability
        if (info.modelProperty.isWriteable()) {
            // might be two way, but take transformer into account
            info.isTwoWay = info.transformer == null || info.transformer instanceof TwoWayBindingTransformer;
        }

        // determine if the controller is accessed
        info.accessesController = Boolean.TRUE
                .equals(memberExp.getInstance().accept(new IsControllerAccessedVisitor()));

        // create binding
        Function<Object[], ?> instanceFunction = exp.withBody(memberExp.getInstance()).compile();
        info.propertyOwnerSupplier = () -> instanceFunction.apply(new Object[] {});

        return Try.of(info);
    }

    private static class IsControllerAccessedVisitor extends NullExpressionVisitor<Boolean> {

        @Override
        public Boolean visit(MemberExpression e) {
            if (e.getExpressionType() == MemberExpressionType.FieldAccess && controllerField.equals(e.getMember())
                    && e.getInstance() instanceof ThisExpression)
                return true;
            return e.getInstance().accept(this);
        }

        @Override
        public Boolean visit(UnaryExpression e) {
            if (e.getExpressionType() == UnaryExpressionType.Convert)
                return e.getFirst().accept(this);
            return null;
        }
    }

    private static class MemberExpressionExtractor extends NullExpressionVisitor<MemberExpression> {
        @Override
        public MemberExpression visit(MemberExpression e) {
            return e;
        }

        @Override
        public MemberExpression visit(UnaryExpression e) {
            if (e.getExpressionType() == UnaryExpressionType.Convert)
                return e.getFirst().accept(this);
            return null;
        }

    }

}
