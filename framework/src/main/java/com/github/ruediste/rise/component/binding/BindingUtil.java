package com.github.ruediste.rise.component.binding;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.lambdaInspector.Lambda;
import com.github.ruediste.lambdaInspector.LambdaAccessedMemberAnalyzer;
import com.github.ruediste.lambdaInspector.LambdaInspector;
import com.github.ruediste.lambdaInspector.LambdaStatic.LambdaAccessedMemberInfo;
import com.github.ruediste.lambdaInspector.expr.MethodInvocationExpression;
import com.github.ruediste.rise.util.Try;

@Singleton
public class BindingUtil {

    @Inject
    PropertyUtil propertyUtil;

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
        Lambda inspect = LambdaInspector.inspect(lambda);

        LambdaAccessedMemberInfo memberExp = inspect.static_.accessedMemberInfo;
        if (memberExp == null)
            return Try.failure(() -> new RuntimeException("Unable to extract binding info from " + lambda));

        // check for a transformer
        if (transformMethod.equals(memberExp.member)) {
            info.transformer = (BindingTransformer<?, ?>) inspect.memberHandle.getBase();
            memberExp = LambdaAccessedMemberAnalyzer.analyze(((MethodInvocationExpression) memberExp.expr).args.get(0));
        }

        // get accessed property
        Optional<PropertyInfo> modelProperty = PropertyUtil.tryGetProperty(memberExp.member);
        if (!modelProperty.isPresent()) {
            Member tmp = memberExp.member;
            return Try.failure(() -> new RuntimeException("Unable to get property for " + tmp));
        }
        info.modelProperty = modelProperty.get();

        // determine writeability
        if (info.modelProperty.isWriteable()) {
            // might be two way, but take transformer into account
            info.isTwoWay = info.transformer == null || info.transformer instanceof TwoWayBindingTransformer;
        }

        // set property owner supplier
        {
            LambdaAccessedMemberInfo tmp = memberExp;
            info.propertyOwnerSupplier = () -> tmp.getBase(inspect, new Object[] {});
        }

        return Try.of(info);
    }

}
