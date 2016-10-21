package com.github.ruediste.rise.component.binding;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.nonReloadable.lambda.LambdaExpression;
import com.github.ruediste.rise.nonReloadable.lambda.expression.Expression;
import com.github.ruediste.rise.nonReloadable.lambda.expression.ExpressionVisitorAdapter;
import com.github.ruediste.rise.nonReloadable.lambda.expression.MemberExpression;
import com.github.ruediste.rise.nonReloadable.lambda.expression.ThisExpression;
import com.github.ruediste.rise.nonReloadable.lambda.expression.UnaryExpression;
import com.github.ruediste.rise.nonReloadable.lambda.expression.UnaryExpressionType;
import com.github.ruediste.rise.util.Try;

@Singleton
public class BindingUtil {

    @Inject
    PropertyUtil propertyUtil;

    public static final MemberExpressionExtractor MEMBER_EXPRESSION_EXTRACTOR = new MemberExpressionExtractor();
    private static final Method transformMethod;

    static {
        try {
            transformMethod = BindingTransformer.class.getMethod("transform", Object.class);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> BindingInfo<T> extractBindingInfo(Supplier<T> lambda) {
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

        // set property owner supplier
        Function<Object[], ?> instanceFunction = exp.withBody(memberExp.getInstance()).compile();
        info.propertyOwnerSupplier = () -> instanceFunction.apply(new Object[] {});

        // extract model path
        info.modelPropertyPath = memberExp.accept(new PathExtractor());

        return Try.of(info);
    }

    private static class PathExtractor extends ExpressionVisitorAdapter<Optional<String>> {
        @Override
        public Optional<String> visit(Expression e) {
            return Optional.empty();
        }

        @Override
        public Optional<String> visit(ThisExpression e) {
            return Optional.of("");
        }

        @Override
        public Optional<String> visit(MemberExpression e) {
            switch (e.getExpressionType()) {
            case FieldAccess:
                return e.getInstance().accept(this).map(x -> (x.isEmpty() ? "" : x + ".") + e.getMember().getName());
            case MethodAccess: {
                Optional<String> base;
                if (e.getInstance() != null)
                    base = e.getInstance().accept(this);
                else
                    base = Optional.of("this.");
                return base.flatMap(x -> PropertyUtil.tryGetProperty(e.getMember())
                        .map(i -> (x.isEmpty() ? "" : x + ".") + i.getName()));
            }
            default:
                return Optional.empty();
            }
        }
    }

    private static class MemberExpressionExtractor extends ExpressionVisitorAdapter<MemberExpression> {
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
