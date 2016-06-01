/*
 * Copyright TrigerSoft <kostat@trigersoft.com> 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.ruediste.rise.nonReloadable.lambda.expression;

import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.add;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.and;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.bitwiseAnd;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.bitwiseNot;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.bitwiseOr;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.constant;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.divide;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.equal;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.greaterThan;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.greaterThanOrEqual;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.iif;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.instanceOf;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.lessThan;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.lessThanOrEqual;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.modulo;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.multiply;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.negate;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.not;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.or;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.shiftLeft;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.shiftRight;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.subtract;
import static com.github.ruediste.rise.nonReloadable.lambda.expression.Functions.xor;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import com.github.ruediste.rise.nonReloadable.lambda.LambdaExpression;

/**
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

public final class LambdaExpressionCompiler implements ExpressionVisitor<Function<Object[], ?>> {

    private LambdaExpression<?> lambdaExpression;

    public LambdaExpressionCompiler(LambdaExpression<?> lambdaExpression) {
        this.lambdaExpression = lambdaExpression;
    }

    private Function<Object[], ?> normalize(BiFunction<Object[], Object[], ?> source) {
        return pp -> source.apply(pp, pp);
    }

    private Function<Object[], Boolean> normalize(BiPredicate<Object[], Object[]> source) {
        return pp -> source.test(pp, pp);
    }

    private Function<Object[], Boolean> normalize(Predicate<Object[]> source) {
        return pp -> source.test(pp);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Function<Object[], ?> visit(BinaryExpression e) {
        final Function<Object[], ?> first = e.getFirst().accept(this);
        final Function<Object[], ?> second = e.getSecond().accept(this);
        switch (e.getExpressionType()) {
        case Add:
            return normalize(add((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case BitwiseAnd:
            return normalize(bitwiseAnd((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case LogicalAnd:
            return normalize(and((Function<Object[], Boolean>) first, (Function<Object[], Boolean>) second));
        case ArrayIndex:
            return t -> Array.get(first.apply(t), (Integer) second.apply(t));
        case Divide:
            return normalize(divide((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case Equal:
            return normalize(equal(first, second));
        case ExclusiveOr:
            return normalize(xor((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case GreaterThan:
            return normalize(greaterThan((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case GreaterThanOrEqual:
            return normalize(
                    greaterThanOrEqual((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case LeftShift:
            return normalize(shiftLeft((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case LessThan:
            return normalize(lessThan((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case LessThanOrEqual:
            return normalize(lessThanOrEqual((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case Modulo:
            return normalize(modulo((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case Multiply:
            return normalize(multiply((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case NotEqual:
            return normalize(equal(first, second).negate());
        case BitwiseOr:
            return normalize(bitwiseOr((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case LogicalOr:
            return normalize(or((Function<Object[], Boolean>) first, (Function<Object[], Boolean>) second));
        // case Power:
        // return power((Function<Number, Object[]>) first,
        // (Function<Number, Object[]>) second);
        case RightShift:
            return normalize(shiftRight((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case Subtract:
            return normalize(subtract((Function<Object[], Number>) first, (Function<Object[], Number>) second));
        case InstanceOf:
            return normalize(instanceOf(first, (Class<?>) second.apply(null)));
        default:
            throw new IllegalArgumentException(e.getExpressionType().toString());
        }
    }

    @Override
    public Function<Object[], ?> visit(ConstantExpression e) {
        return constant(e.getValue());
    }

    @Override
    public Function<Object[], ?> visit(MemberExpression e) {
        final Member m = e.getMember();
        Expression ei = e.getInstance();
        final Function<Object[], ?> instance = ei != null ? ei.accept(this) : null;

        List<Function<Object[], ?>> argExps = e.getArguments().stream()
                .map(exp -> ((Function<Object[], ?>) exp.accept(this))).collect(toList());
        Function<Object[], Object[]> args = t -> argExps.stream().map(x -> x.apply(t)).collect(toList()).toArray();

        Function<Object[], Object> field = t -> {
            try {
                return ((Field) m).get(instance == null ? null : instance.apply(t));
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        };

        Function<Object[], ?> method = t -> {
            Object inst;
            if (instance != null) {
                inst = instance.apply(t);
            } else
                inst = null;
            try {
                return ((Method) m).invoke(inst, args.apply(t));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        };

        Function<Object[], ?> ctor = t -> {
            try {
                return ((Constructor<?>) m).newInstance(args.apply(t));
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        };

        Function<Object[], ?> member;

        if (m instanceof Field)
            member = field;
        else if (m instanceof Method)
            member = method;
        else
            member = ctor;

        return member;
    }

    @Override
    public Function<Object[], ?> visit(ParameterExpression e) {
        final int index = e.getIndex();
        return t -> t[index];
    }

    @SuppressWarnings("unchecked")
    @Override
    public Function<Object[], ?> visit(UnaryExpression e) {
        final Function<Object[], ?> first = e.getFirst().accept(this);
        switch (e.getExpressionType()) {
        case ArrayLength:
            return t -> Array.getLength(first.apply(t));
        case BitwiseNot:
            return (Function<Object[], ?>) bitwiseNot((Function<Object[], Number>) first);
        case Convert:
            final Class<?> to = e.getResultType();
            if (to.equals(Boolean.TYPE) && e.getFirst().getResultType().equals(Integer.TYPE)) {
                return t -> (Integer.valueOf(1).equals(first.apply(t)));
            }
            if (to.isPrimitive() || Number.class.isAssignableFrom(to))
                return t -> {
                    Object source = first.apply(t);
                    if (source instanceof Number) {
                        Number result = (Number) source;
                        if (to.isPrimitive()) {
                            if (to == Integer.TYPE)
                                return result.intValue();
                            if (to == Long.TYPE)
                                return result.longValue();
                            if (to == Float.TYPE)
                                return result.floatValue();
                            if (to == Double.TYPE)
                                return result.doubleValue();
                            if (to == Byte.TYPE)
                                return result.byteValue();
                            if (to == Character.TYPE)
                                return (char) result.intValue();
                            if (to == Short.TYPE)
                                return result.shortValue();
                        } else if (result != null) {
                            if (to == BigInteger.class)
                                return BigInteger.valueOf(result.longValue());
                            if (to == BigDecimal.class)
                                return BigDecimal.valueOf(result.doubleValue());
                        }
                    }
                    if (source instanceof Character) {
                        if (to == Integer.TYPE)
                            return (int) (char) source;
                        if (to == Long.TYPE)
                            return (long) (char) source;
                        if (to == Float.TYPE)
                            return (float) (char) source;
                        if (to == Double.TYPE)
                            return (double) (char) source;
                    }
                    return to.cast(source);
                };

            return first;
        case IsNull:
            return first.andThen(r -> r == null);
        case LogicalNot:
            return normalize(not((Function<Object[], Boolean>) first));
        case Negate:
            return (Function<Object[], ?>) negate((Function<Object[], Number>) first);
        default:
            throw new IllegalArgumentException(e.getExpressionType().toString());
        }
    }

    @Override
    public Function<Object[], ?> visit(ThisExpression e) {
        return t -> lambdaExpression.getThis();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Function<Object[], ?> visit(TenaryExpression e) {
        return iif((Function<Object[], Boolean>) e.getFirst().accept(this), e.getSecond().accept(this),
                e.getThird().accept(this));
    }

    @Override
    public Function<Object[], ?> visit(CapturedArgumentExpression e) {
        return t -> lambdaExpression.getValue(e);
    }

    @Override
    public Function<Object[], ?> visit(GetLocalVariableExpression getLocalVariableExpression) {
        throw new UnsupportedOperationException(
                "all getLocalVariable expressions should have been replaced with this, captured argument or parameter expressions");
    }
}
