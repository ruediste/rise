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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.github.ruediste.rise.nonReloadable.lambda.LambdaExpression;
import com.google.common.reflect.Reflection;

/**
 * Provides the base class from which the classes that represent expression tree
 * nodes are derived. It also contains static factory methods to create the
 * various node types.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */
public abstract class Expression {
    private final Class<?> _resultType;

    static private final HashMap<Method, Class<?>> _boxers;
    static private final HashMap<Method, Class<?>> _unboxers;

    static {

        HashMap<Method, Class<?>> unboxers = new HashMap<Method, Class<?>>(8);
        try {
            unboxers.put(Boolean.class.getMethod("booleanValue"), Boolean.TYPE);
            unboxers.put(Byte.class.getMethod("byteValue"), Byte.TYPE);
            unboxers.put(Character.class.getMethod("charValue"), Character.TYPE);
            unboxers.put(Double.class.getMethod("doubleValue"), Double.TYPE);
            unboxers.put(Float.class.getMethod("floatValue"), Float.TYPE);
            unboxers.put(Integer.class.getMethod("intValue"), Integer.TYPE);
            unboxers.put(Long.class.getMethod("longValue"), Long.TYPE);
            unboxers.put(Short.class.getMethod("shortValue"), Short.TYPE);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        HashMap<Method, Class<?>> boxers = new HashMap<Method, Class<?>>(8);
        try {
            boxers.put(Boolean.class.getMethod("valueOf", Boolean.TYPE), Boolean.class);
            boxers.put(Byte.class.getMethod("valueOf", Byte.TYPE), Byte.class);
            boxers.put(Character.class.getMethod("valueOf", Character.TYPE), Character.class);
            boxers.put(Double.class.getMethod("valueOf", Double.TYPE), Double.class);
            boxers.put(Float.class.getMethod("valueOf", Float.TYPE), Float.class);
            boxers.put(Integer.class.getMethod("valueOf", Integer.TYPE), Integer.class);
            boxers.put(Long.class.getMethod("valueOf", Long.TYPE), Long.class);
            boxers.put(Short.class.getMethod("valueOf", Short.TYPE), Short.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        _unboxers = unboxers;
        _boxers = boxers;
    }

    private boolean isNumeric() {
        return isNumeric(getResultType());
    }

    private boolean isIntegral() {
        return isIntegral(getResultType());
    }

    private static boolean isNumeric(Class<?> type) {
        if (isIntegral(type))
            return true;

        if (type.isPrimitive())
            return type == Float.TYPE || type == Double.TYPE;

        return type == Float.class || type == Double.class || type == BigDecimal.class;
    }

    private static boolean isIntegral(Class<?> type) {
        if (!type.isPrimitive())
            return type == Byte.class || type == Integer.class || type == Long.class || type == Short.class
                    || type == BigInteger.class;

        return type == Byte.TYPE || type == Integer.TYPE || type == Long.TYPE || type == Short.TYPE;
    }

    private boolean isBoolean() {
        return isBoolean(getResultType());
    }

    private static boolean isBoolean(Class<?> type) {
        return type == Boolean.TYPE || type == Boolean.class;
    }

    public static Expression stripConverts(Expression e) {
        while (e instanceof UnaryExpression && ((UnaryExpression) e).getExpressionType() == UnaryExpressionType.Convert)
            e = ((UnaryExpression) e).getFirst();

        return e;
    }

    /**
     * Gets the static type of the expression that this {@link ExpressionType}
     * represents.
     * 
     * @return The {@link Class} that represents the static type of the
     *         expression.
     */
    public final Class<?> getResultType() {
        return _resultType;
    }

    /**
     * Initializes a new instance of the {@link Expression} class.
     * 
     * @param resultType
     *            The {@link Class} to set as the type of the expression that
     *            this Expression represents.
     */
    protected Expression(Class<?> resultType) {

        if (resultType == null)
            throw new NullPointerException("resultType");

        _resultType = resultType;
    }

    /**
     * Creates a {@link BinaryExpression} that represents an arithmetic addition
     * operation that does not have overflow checking.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         property equal to Add and the getFirst() and getSecond() methods
     *         set to the specified values.
     */
    public static BinaryExpression add(Expression first, Expression second) {
        return createNumeric(BinaryExpressionType.Add, first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents an arithmetic division
     * operation.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         property equal to Divide and the getFirst() and getSecond()
     *         methods set to the specified values.
     */
    public static BinaryExpression divide(Expression first, Expression second) {
        return createNumeric(BinaryExpressionType.Divide, first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents an arithmetic subtract
     * operation.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         property equal to Subtract and the getFirst() and getSecond()
     *         methods set to the specified values.
     */
    public static BinaryExpression subtract(Expression first, Expression second) {
        return createNumeric(BinaryExpressionType.Subtract, first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents an arithmetic multiply
     * operation.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         property equal to Multiply and the getFirst() and getSecond()
     *         methods set to the specified values.
     */
    public static BinaryExpression multiply(Expression first, Expression second) {
        return createNumeric(BinaryExpressionType.Multiply, first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents an arithmetic
     * remainder operation.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         property equal to Modulo and the getFirst() and getSecond()
     *         methods set to the specified values.
     */
    public static BinaryExpression modulo(Expression first, Expression second) {
        return createNumeric(BinaryExpressionType.Modulo, first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents a "greater than"
     * numeric comparison.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         property equal to GreaterThan and the getFirst() and getSecond()
     *         methods set to the specified values.
     */
    public static BinaryExpression greaterThan(Expression first, Expression second) {
        return createNumericComparison(BinaryExpressionType.GreaterThan, first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents a "greater than or
     * equal" numeric comparison.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         property equal to GreaterThanOrEqual and the getFirst() and
     *         getSecond() methods set to the specified values.
     */
    public static BinaryExpression greaterThanOrEqual(Expression first, Expression second) {
        return createNumericComparison(BinaryExpressionType.GreaterThanOrEqual, first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents a "less than" numeric
     * comparison.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         property equal to LessThan and the getFirst() and getSecond()
     *         methods set to the specified values.
     */
    public static BinaryExpression lessThan(Expression first, Expression second) {
        return createNumericComparison(BinaryExpressionType.LessThan, first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents a "less than or equal"
     * numeric comparison.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         property equal to LessThanOrEqual and the getFirst() and
     *         getSecond() methods set to the specified values.
     */
    public static BinaryExpression lessThanOrEqual(Expression first, Expression second) {
        return createNumericComparison(BinaryExpressionType.LessThanOrEqual, first, second);
    }

    private static BinaryExpression createNumericComparison(BinaryExpressionType expressionType, Expression first,
            Expression second) {
        if (!first.isNumeric())
            throw new IllegalArgumentException(first.getResultType().toString());
        if (!second.isNumeric())
            throw new IllegalArgumentException(second.getResultType().toString());

        return new BinaryExpression(expressionType, Boolean.TYPE, first, second);
    }

    private static BinaryExpression createNumeric(BinaryExpressionType expressionType, Expression first,
            Expression second) {
        boolean fnumeric = first.isNumeric();
        boolean snumeric = second.isNumeric();
        if (!fnumeric || !snumeric) {
            if (!fnumeric && !snumeric)
                throw new IllegalArgumentException("At least one argument must be numeric, got: "
                        + first.getResultType().toString() + "," + second.getResultType().toString());
            if (!fnumeric)
                first = TypeConverter.convert(first, second.getResultType());
            else
                second = TypeConverter.convert(second, first.getResultType());
        }

        return new BinaryExpression(expressionType, first.getResultType(), first, second);
    }

    private static BinaryExpression createIntegral(BinaryExpressionType expressionType, Expression first,
            Expression second) {
        if (!first.isIntegral())
            throw new IllegalArgumentException(first.getResultType().toString());
        if (!second.isIntegral())
            throw new IllegalArgumentException(second.getResultType().toString());

        return new BinaryExpression(expressionType, first.getResultType(), first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents an arithmetic
     * left-shift operation.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         property equal to LeftShift and the getFirst() and getSecond()
     *         methods set to the specified values.
     */
    public static BinaryExpression leftShift(Expression first, Expression second) {
        return createIntegral(BinaryExpressionType.LeftShift, first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents an arithmetic
     * right-shift operation.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         property equal to RightShift and the getFirst() and getSecond()
     *         methods set to the specified values.
     */
    public static BinaryExpression rightShift(Expression first, Expression second) {
        return createIntegral(BinaryExpressionType.RightShift, first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents a coalescing
     * operation.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         property equal to Coalesce and the getFirst() and getSecond()
     *         methods set to the specified values.
     */
    public static BinaryExpression coalesce(Expression first, Expression second) {
        if (first.getResultType().isPrimitive())
            throw new IllegalArgumentException(first.getResultType().toString());
        if (second.getResultType().isPrimitive())
            throw new IllegalArgumentException(second.getResultType().toString());
        return new BinaryExpression(BinaryExpressionType.Coalesce, first.getResultType(), first, second);
    }

    /**
     * Creates a {@link Expression} that represents an equality comparison. The
     * expression will be simplified if one of parameters is constant
     * {@link Boolean}.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         method equal to Equal and the getFirst() and getSecond() methods
     *         set to the specified values, or one of the parameters if they one
     *         of them is constant {@link Boolean}.
     */
    public static Expression equal(Expression first, Expression second) {
        if (first.getResultType() != second.getResultType())
            throw new IllegalArgumentException(
                    first.getResultType().toString() + " != " + second.getResultType().toString());
        return createBooleanExpression(BinaryExpressionType.Equal, first, second);
    }

    /**
     * Creates a {@link Expression} that represents an inequality comparison.
     * The expression will be simplified if one of parameters is constant
     * {@link Boolean}.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         method equal to Equal and the getFirst() and getSecond() methods
     *         set to the specified values, or one of the parameters if they one
     *         of them is constant {@link Boolean}.
     */
    public static Expression notEqual(Expression first, Expression second) {
        if (first.getResultType() != second.getResultType())
            throw new IllegalArgumentException(
                    first.getResultType().toString() + " != " + second.getResultType().toString());
        return createBooleanExpression(BinaryExpressionType.NotEqual, first, second);
    }

    /**
     * Creates a {@link Expression} that represents a conditional AND operation
     * that evaluates the second operand only if it has to. The expression will
     * be simplified if one of parameters is constant.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         method equal to LogicalAnd and the getFirst() and getSecond()
     *         methods set to the specified values, or one of the parameters if
     *         they one of them is constant.
     */
    public static Expression logicalAnd(Expression first, Expression second) {
        if (!first.isBoolean())
            throw new IllegalArgumentException(first.getResultType().toString());
        if (!second.isBoolean())
            throw new IllegalArgumentException(second.getResultType().toString());
        return createBooleanExpression(BinaryExpressionType.LogicalAnd, first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents a bitwise AND
     * operation.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         method equal to BitwiseAnd and the getFirst() and getSecond()
     *         properties set to the specified values.
     */
    public static BinaryExpression bitwiseAnd(Expression first, Expression second) {
        return createIntegral(BinaryExpressionType.BitwiseAnd, first, second);
    }

    /**
     * Creates a {@link Expression} that represents a conditional OR operation
     * that evaluates the second operand only if it has to. The expression will
     * be simplified if one of parameters is constant.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         method equal to LogicalOr and the getFirst() and getSecond()
     *         methods set to the specified values, or one of the parameters if
     *         they one of them is constant.
     */
    public static Expression logicalOr(Expression first, Expression second) {
        if (!first.isBoolean())
            throw new IllegalArgumentException(first.getResultType().toString());
        if (!second.isBoolean())
            throw new IllegalArgumentException(second.getResultType().toString());
        return createBooleanExpression(BinaryExpressionType.LogicalOr, first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents a bitwise OR
     * operation.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         method equal to BitwiseOr and the getFirst() and getSecond()
     *         properties set to the specified values.
     */
    public static BinaryExpression bitwiseOr(Expression first, Expression second) {
        return createIntegral(BinaryExpressionType.BitwiseOr, first, second);
    }

    /**
     * Creates a {@link BinaryExpression} that represents a bitwise XOR
     * operation, or {@link UnaryExpression} that represents a bitwise NOT in
     * case the second parameter equals to -1.
     * 
     * @param first
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param second
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         method equal to ExclusiveOr and the getFirst() and getSecond()
     *         properties set to the specified values, or
     *         {@link UnaryExpression} that represents a bitwise NOT in case the
     *         second parameter equals to -1.
     */
    public static Expression exclusiveOr(Expression first, Expression second) {
        if (second instanceof ConstantExpression) {
            ConstantExpression csecond = (ConstantExpression) second;
            if (isIntegral(csecond.getResultType())) {
                if (((Number) csecond.getValue()).intValue() == -1)
                    return bitwiseNot(first);
            }
        }

        return createIntegral(BinaryExpressionType.ExclusiveOr, first, second);
    }

    /**
     * Creates a {@link UnaryExpression} that represents getting the length of
     * an array.
     * 
     * @param array
     *            An {@link Expression} to set the getFirst method equal to.
     * @return A {@link UnaryExpression} that has the {@link ExpressionType}
     *         property equal to ArrayLength and the getFirst() method set to
     *         array.
     */
    public static UnaryExpression arrayLength(Expression array) {
        if (!array.getResultType().isArray())
            throw new IllegalArgumentException(array.getResultType().toString());

        return new UnaryExpression(UnaryExpressionType.ArrayLength, Integer.TYPE, array);
    }

    /**
     * Creates a {@link BinaryExpression} that represents applying an array
     * index operator to an array.
     * 
     * @param array
     *            An Expression to set the getFirst method equal to.
     * @param index
     *            An Expression to set the getSecond method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         method equal to ArrayIndex and the getFirst() and getSecond()
     *         methods set to the specified values.
     */
    public static BinaryExpression arrayIndex(Expression array, Expression index) {
        Class<?> arrayType = array.getResultType();
        if (!arrayType.isArray())
            throw new IllegalArgumentException(arrayType.toString());

        if (index.getResultType() != Integer.TYPE)
            throw new IllegalArgumentException("index:" + index.getResultType().toString());

        return new BinaryExpression(BinaryExpressionType.ArrayIndex, arrayType.getComponentType(), array, index);
    }

    /**
     * Creates a {@link UnaryExpression} that represents a conversion operation,
     * or 'e' if its ResultType equals to 'to'.
     * 
     * @param e
     *            An Expression to set the getFirst() method equal to.
     * @param to
     *            The {@link Class} to set as the type of the expression that
     *            this Expression represents.
     * @return A {@link UnaryExpression} that has the {@link ExpressionType}
     *         property equal to Convert, or 'e'.
     */
    public static Expression convert(Expression e, Class<?> to) {
        if (to.isAssignableFrom(e.getResultType()))
            return e;
        return new UnaryExpression(UnaryExpressionType.Convert, to, e);
    }

    /**
     * Creates a {@link ConstantExpression} that has the getValue() method set
     * to the specified value and resultType is assignable from its type.
     * 
     * @param value
     *            An Object to set the getValue() method equal to.
     * @param resultType
     *            The {@link Class} to set as the type of the expression that
     *            this Expression represents.
     * @return A {@link ConstantExpression} that has the {@link ExpressionType}
     *         property equal to Constant and the getValue() method set to the
     *         specified value.
     */
    public static ConstantExpression constant(Object value, Class<?> resultType) {
        return new ConstantExpression(resultType, value);
    }

    /**
     * Creates a {@link ConstantExpression} that has the getValue() method set
     * to the specified value.
     * 
     * @param value
     *            An Object to set the getValue() method equal to.
     * @return A {@link ConstantExpression} that has the {@link ExpressionType}
     *         property equal to Constant and the getValue() method set to the
     *         specified value.
     */
    public static ConstantExpression constant(Object value) {
        Class<?> type = value == null ? Object.class : value.getClass();
        return constant(value, type);
    }

    /**
     * Creates a {@link UnaryExpression} that represents an arithmetic negation
     * operation.
     * 
     * @param e
     *            An {@link Expression} to set the getValue() method equal to.
     * @return A {@link UnaryExpression} that has the {@link ExpressionType}
     *         property equal to Negate and the getValue() method set to the
     *         specified value.
     */
    public static UnaryExpression negate(Expression e) {
        if (!e.isNumeric())
            throw new IllegalArgumentException(e.getResultType().toString());
        return new UnaryExpression(UnaryExpressionType.Negate, e.getResultType(), e);
    }

    /**
     * Creates a {@link ParameterExpression}.
     * 
     * @param resultType
     *            The {@link Class} to set as the type of the expression that
     *            this Expression represents.
     * @param index
     *            Parameter index in the method signature.
     * @return A {@link ParameterExpression} that has the getExpressionType()
     *         method equal to Parameter and the getResultType() and getIndex()
     *         methods set to the specified values.
     */
    public static ParameterExpression parameter(Class<?> resultType, int index) {
        return new ParameterExpression(resultType, index);
    }

    /**
     * Creates a {@link BinaryExpression} that represents an instanceOf test.
     * 
     * @param e
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param type
     *            The {@link Class} that assignability is tested with.
     * @return A {@link BinaryExpression} that has the getExpressionType() equal
     *         to InstanceOf, the getFirst() set to 'e' and getSecond() set to
     *         {@link ConstantExpression} with value equals to 'type'.
     */
    public static BinaryExpression instanceOf(Expression e, Class<?> type) {
        return instanceOf(e, constant(type));
    }

    /**
     * Creates a {@link BinaryExpression} that represents an instanceOf test.
     * 
     * @param e
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param type
     *            The {@link Expression} that evaluates to Class assignability
     *            is tested with.
     * @return A {@link BinaryExpression} that has the getExpressionType() equal
     *         to InstanceOf, the getFirst() set to 'e' and getSecond() set to
     *         {@link ConstantExpression} with value equals to 'type'.
     */
    public static BinaryExpression instanceOf(Expression e, Expression type) {
        return new BinaryExpression(BinaryExpressionType.InstanceOf, Boolean.TYPE, e, type);
    }

    /**
     * Creates a {@link Expression}, given an operand and unary operator, by
     * calling the appropriate factory method.
     * 
     * @param expressionType
     *            The {@link ExpressionType} that specifies the type of unary
     *            operation.
     * @param resultType
     *            The {@link Class} that specifies the type to be converted to
     *            (pass null if not applicable).
     * @param operand
     *            An {@link Expression} that represents the operand.
     * @return The {@link Expression} that results from calling the appropriate
     *         factory method.
     */
    public static Expression unary(UnaryExpressionType expressionType, Class<?> resultType, Expression operand) {
        switch (expressionType) {
        case Convert:
            return convert(operand, resultType);
        case ArrayLength:
            return arrayLength(operand);
        case Negate:
            return negate(operand);
        case BitwiseNot:
            return bitwiseNot(operand);
        case LogicalNot:
            return logicalNot(operand);
        case IsNull:
            return isNull(operand);
        default:
            throw new IllegalArgumentException("expressionType");
        }
    }

    /**
     * Creates a {@link Expression}, given an operand and binary operator, by
     * calling the appropriate factory method.
     * 
     * @param expressionType
     *            The {@link ExpressionType} that specifies the type of binary
     *            operation.
     * @param first
     *            An {@link Expression} that represents the left operand.
     * @param second
     *            An {@link Expression} that represents the right operand.
     * @return The {@link Expression} that results from calling the appropriate
     *         factory method.
     */
    public static Expression binary(BinaryExpressionType expressionType, Expression first, Expression second) {

        switch (expressionType) {
        case Add:
            return add(first, second);
        case BitwiseAnd:
            return bitwiseAnd(first, second);
        case LogicalAnd:
            return logicalAnd(first, second);
        case ArrayIndex:
            return arrayIndex(first, second);
        case Coalesce:
            return coalesce(first, second);
        case Divide:
            return divide(first, second);
        case Equal:
            return equal(first, second);
        case ExclusiveOr:
            return exclusiveOr(first, second);
        case GreaterThan:
            return greaterThan(first, second);
        case GreaterThanOrEqual:
            return greaterThanOrEqual(first, second);
        case LeftShift:
            return leftShift(first, second);
        case LessThan:
            return lessThan(first, second);
        case LessThanOrEqual:
            return lessThanOrEqual(first, second);
        case Modulo:
            return modulo(first, second);
        case Multiply:
            return multiply(first, second);
        case NotEqual:
            return notEqual(first, second);
        case BitwiseOr:
            return bitwiseOr(first, second);
        case LogicalOr:
            return logicalOr(first, second);
        case RightShift:
            return rightShift(first, second);
        case Subtract:
            return subtract(first, second);
        case InstanceOf:
            return instanceOf(first, second);
        default:
            throw new IllegalArgumentException("expressionType");
        }
    }

    private static Expression createBooleanExpression(BinaryExpressionType expressionType, Expression first,
            Expression second) {

        return new BinaryExpression(expressionType, Boolean.TYPE, first, second);
    }

    /**
     * Creates a {@link InvocationExpression} that represents accessing a static
     * field given the name of the field.
     * 
     * @param type
     *            The {@link Class} that specifies the type that contains the
     *            specified static field.
     * @param name
     *            The name of a field.
     * @return A {@link InvocationExpression} that represents accessing a static
     *         field given the name of the field.
     * @throws NoSuchFieldException
     *             if a field with the specified name is not found.
     */
    public static InvocationExpression get(Class<?> type, String name) throws NoSuchFieldException {
        return get(null, type.getDeclaredField(name));
    }

    /**
     * Creates a {@link InvocationExpression} that represents accessing an
     * instance field given the name of the field.
     * 
     * @param instance
     *            An {@link Expression} whose {@code getResultType()} value will
     *            be searched for a specific field.
     * @param name
     *            The name of a field.
     * @return A {@link InvocationExpression} that represents accessing an
     *         instance field given the name of the field.
     * @throws NoSuchFieldException
     *             if a field with the specified name is not found.
     */
    public static InvocationExpression get(Class<?> context, Class<?> owner, Expression instance, String name)
            throws NoSuchFieldException {
        Field declaredField = lookupField(context, owner, name);
        if (declaredField == null)
            throw new RuntimeException("Error while creating get field expression: no field named " + name
                    + " found on " + instance.getResultType());
        return get(instance, declaredField);
    }

    private static Field lookupField(Class<?> context, Class<?> owner, String name) {
        if (owner == null)
            return null;
        for (Field field : owner.getDeclaredFields()) {
            if (!field.getName().equals(name))
                continue;
            int modifiers = field.getModifiers();
            boolean isPrivate = Modifier.isPrivate(modifiers);
            if (isPrivate && !context.equals(owner)) {
                continue;
            }

            boolean isSamePackage = Reflection.getPackageName(owner).equals(Reflection.getPackageName(context));
            boolean isProtected = Modifier.isProtected(modifiers);
            if (isProtected && !owner.isAssignableFrom(context) && !isSamePackage) {
                continue;
            }

            if (!(isPrivate || isProtected || Modifier.isPublic(modifiers)) && !isSamePackage)
                continue;
            return field;

        }
        return lookupField(context, owner.getSuperclass(), name);
    }

    /**
     * Creates a {@link MemberExpression} that accessed the specified member.
     * 
     * @param expressionType
     *            Type of access.
     * @param instance
     *            An {@link Expression} representing the instance.
     * @param member
     *            The {@code Member} to be accessed.
     * @param resultType
     *            The return value type.
     * @param parameterTypes
     *            The parameters.
     * @param arguments
     * @return A {@link MemberExpression} that accessed the specified member.
     */
    public static MemberExpression member(MemberExpressionType expressionType, Expression instance, Member member,
            Class<?> resultType, List<Class<?>> parameterTypes, List<Expression> arguments) {
        return new MemberExpression(expressionType, instance, member, resultType, parameterTypes, arguments);
    }

    /**
     * Creates a {@link InvocationExpression} that represents accessing an
     * instance field.
     * 
     * @param instance
     *            An {@link Expression} representing the instance.
     * @param field
     *            A field to be accessed.
     * @return An {@link InvocationExpression} that represents accessing an
     *         instance field.
     */
    public static InvocationExpression get(Expression instance, Field field) {
        return new MemberExpression(MemberExpressionType.FieldAccess, instance, field, field.getType(),
                Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Creates an {@link InvocationExpression} that represents a call to an
     * instance method, or {@link UnaryExpression} in case of boxing.
     * 
     * @param instance
     *            An {@link Expression} whose {@code getResultType()} value will
     *            be searched for a specific method.
     * @param method
     *            The {@link Method} to be called.
     * @param arguments
     *            An array of {@link Expression} objects that represent the
     *            arguments to the method.
     * @return An {@link InvocationExpression} that has the
     *         {@link ExpressionType} method equal to Invoke or Convert in case
     *         of boxing.
     */
    public static Expression invoke(Expression instance, Method method, Expression... arguments) {
        return invoke(instance, method, Collections.unmodifiableList(Arrays.asList(arguments)));
    }

    /**
     * Creates an {@link InvocationExpression} that represents a call to an
     * instance method, or {@link UnaryExpression} in case of boxing.
     * 
     * @param instance
     *            An {@link Expression} whose {@code getResultType()} value will
     *            be searched for a specific method.
     * @param method
     *            The {@link Method} to be called.
     * @param arguments
     *            An array of {@link Expression} objects that represent the
     *            arguments to the method.
     * @return An {@link InvocationExpression} that has the
     *         {@link ExpressionType} method equal to Invoke or Convert in case
     *         of boxing.
     */
    public static Expression invoke(Expression instance, Method method, List<Expression> arguments) {

        // check if we're just unboxing a primitive and replace it with a cast
        if (instance != null && !instance.getResultType().isPrimitive()) {
            Class<?> primitive = _unboxers.get(method);
            if (primitive != null) {
                return convert(instance, primitive);
            }
        }

        // replace calls to the boxing operations with a conversion
        if (instance == null && arguments.size() == 1 && arguments.get(0).getResultType().isPrimitive()) {
            Class<?> boxedType = _boxers.get(method);
            if (boxedType != null)
                return convert(arguments.get(0), boxedType);

        }

        return new MemberExpression(MemberExpressionType.MethodAccess, instance, method, method.getReturnType(),
                Arrays.asList(method.getParameterTypes()), arguments);
    }

    /**
     * Creates a {@link InvocationExpression} that represents calling the
     * specified constructor.
     * 
     * @param method
     *            The constructor to invoke.
     * @param arguments
     *            The constructor arguments.
     * @return A {@link InvocationExpression} that represents calling the
     *         specified constructor.
     */
    public static InvocationExpression newInstance(Constructor<?> method, Expression... arguments) {
        return newInstance(method, Collections.unmodifiableList(Arrays.asList(arguments)));
    }

    /**
     * Creates a {@link InvocationExpression} that represents calling the
     * specified constructor.
     * 
     * @param method
     *            The constructor to invoke.
     * @param arguments
     *            The constructor arguments.
     * @return A {@link InvocationExpression} that represents calling the
     *         specified constructor.
     */
    public static InvocationExpression newInstance(Constructor<?> method, List<Expression> arguments) {
        return new MemberExpression(MemberExpressionType.New, null, method, method.getDeclaringClass(),
                Arrays.asList(method.getParameterTypes()), arguments);
    }

    /**
     * Creates a {@link InvocationExpression} that represents calling the
     * specified constructor.
     * 
     * @param type
     *            {@link Class} to be instantiated.
     * @param argumentTypes
     *            The Constructor argument types.
     * @param arguments
     *            The constructor arguments.
     * @return A {@link InvocationExpression} that represents calling the
     *         specified constructor.
     * @throws NoSuchMethodException
     *             if a matching method is not found.
     */
    public static InvocationExpression newInstance(Class<?> type, Class<?>[] argumentTypes, Expression... arguments)
            throws NoSuchMethodException {

        return newInstance(type.getConstructor(argumentTypes), arguments);
    }

    /**
     * Creates an {@link InvocationExpression} that represents a call to an
     * instance method by calling the appropriate factory method, or
     * {@link UnaryExpression} in case of boxing.
     * 
     * @param instance
     *            An {@link Expression} whose {@code getResultType()} value will
     *            be searched for a specific method.
     * @param name
     *            The name of the method.
     * @param parameterTypes
     *            An array of {@link Class} objects that specify the type of
     *            parameters of the method.
     * @param arguments
     *            An array of {@link Expression} objects that represent the
     *            arguments to the method.
     * @return An {@link InvocationExpression} that has the
     *         {@link ExpressionType} method equal to Invoke or Convert in case
     *         of boxing.
     * @throws NoSuchMethodException
     *             if a matching method is not found.
     */
    public static Expression invoke(Expression instance, String name, Class<?>[] parameterTypes,
            Expression... arguments) throws NoSuchMethodException {
        return invoke(instance, getDeclaredMethod(instance.getResultType(), name, parameterTypes), arguments);
    }

    /**
     * Creates an {@link InvocationExpression} that represents a call to a
     * static method by calling the appropriate factory method, or
     * {@link UnaryExpression} in case of boxing.
     * 
     * @param type
     *            The {@link Class} that specifies the type that contains the
     *            specified static method.
     * @param name
     *            The name of the method.
     * @param parameterTypes
     *            An array of {@link Class} objects that specify the type of
     *            parameters of the method.
     * @param arguments
     *            An array of {@link Expression} objects that represent the
     *            arguments to the method.
     * @return An {@link InvocationExpression} that has the
     *         {@link ExpressionType} method equal to Invoke or Convert in case
     *         of boxing.
     * @throws NoSuchMethodException
     *             if a matching method is not found.
     */
    public static Expression invoke(Class<?> type, String name, Class<?>[] parameterTypes, Expression... arguments)
            throws NoSuchMethodException {
        return invoke(null, getDeclaredMethod(type, name, parameterTypes), arguments);
    }

    /**
     * Get a method declaration recursively starting with the given class.
     * 
     * @param clazz
     *            Class which is the start of the search
     * @param name
     *            The name of the searched method.
     * @param parameterTypes
     *            The parameter types of the searched method.
     * @return The search method declaration.
     * @throws NoSuchMethodException
     *             if a matching method is not found.
     */
    private static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>[] parameterTypes)
            throws NoSuchMethodException {
        Class<?> tmpClass = clazz;

        for (;;) {
            try {
                return tmpClass.getDeclaredMethod(name, parameterTypes);
            } catch (NoSuchMethodException e) {
                tmpClass = tmpClass.getSuperclass();
                if (tmpClass == null)
                    throw e;
            }
        }
    }

    /**
     * Creates a {@link BinaryExpression} that represents a conditional
     * operation, or one of operands in case test is a constant.
     * 
     * @param test
     *            An Expression to set the getOperator() method equal to.
     * @param ifTrue
     *            An {@link Expression} to set the getFirst() method equal to.
     * @param ifFalse
     *            An {@link Expression} to set the getSecond() method equal to.
     * @return A {@link BinaryExpression} that has the {@link ExpressionType}
     *         property equal to Conditional and the getFirst() and getSecond()
     *         methods set to the specified values.
     */
    public static Expression condition(Expression test, Expression ifTrue, Expression ifFalse) {
        if (!test.isBoolean())
            throw new IllegalArgumentException("test is " + test.getResultType());

        return new TenaryExpression(TenaryExpressionType.Conditional, ifTrue.getResultType(), test, ifTrue, ifFalse);
    }

    /**
     * Creates a {@link UnaryExpression} that represents a test for null
     * operation.
     * 
     * @param e
     *            Operand
     * @return A {@link UnaryExpression} that represents a test for null
     *         operation.
     */
    public static UnaryExpression isNull(Expression e) {
        if (e.getResultType().isPrimitive())
            throw new IllegalArgumentException(e.getResultType().toString());

        return new UnaryExpression(UnaryExpressionType.IsNull, Boolean.TYPE, e);
    }

    /**
     * Creates a {@link UnaryExpression} that represents a bitwise complement
     * operation.
     * 
     * @param e
     *            Operand
     * @return A {@link UnaryExpression} that represents a bitwise complement
     *         operation.
     */
    public static UnaryExpression bitwiseNot(Expression e) {
        if (!e.isIntegral())
            throw new IllegalArgumentException(e.getResultType().toString());

        return new UnaryExpression(UnaryExpressionType.BitwiseNot, e.getResultType(), e);
    }

    /**
     * Creates a {@link Expression} that represents a logical negation
     * operation.
     * 
     * @param e
     *            Operand
     * @return A {@link Expression} that represents a logical negation
     *         operation.
     */
    public static Expression logicalNot(Expression e) {
        if (!e.isBoolean())
            throw new IllegalArgumentException(e.getResultType().toString());

        return new UnaryExpression(UnaryExpressionType.LogicalNot, e.getResultType(), e);
    }

    /**
     * Dispatches to the specific visit method for this node type. For example,
     * {@link BinaryExpression} calls the
     * {@link ExpressionVisitor#visit(BinaryExpression)}.
     * 
     * @param <T>
     *            type the visitor methods return after processing.
     * 
     * @param v
     *            The visitor to visit this node with.
     * @return T
     */
    public final <T> T accept(ExpressionVisitor<T> v) {
        return visit(v);
    }

    /**
     * Dispatches to the specific visit method for this node type. For example,
     * {@link BinaryExpression} calls the
     * {@link ExpressionVisitor#visit(BinaryExpression)}.
     * 
     * @param <T>
     *            type the visitor methods return after processing.
     * 
     * @param v
     *            The visitor to visit this node with.
     * @return T
     */
    protected abstract <T> T visit(ExpressionVisitor<T> v);

    @Override
    public int hashCode() {
        return _resultType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Expression))
            return false;
        final Expression other = (Expression) obj;

        return Objects.equals(_resultType, other._resultType);
    }

    public static ThisExpression this_(Class<?> resultType) {
        return new ThisExpression(resultType);
    }

    public static Expression tenary(TenaryExpressionType expressionType, Class<?> resultType, Expression first,
            Expression second, Expression third) {
        return new TenaryExpression(expressionType, resultType, first, second, third);
    }

    public static <T> LambdaExpression<T> lambda(Class<?> resultType, Class<?>[] parameterTypes, Expression _body,
            Object this_, Object[] capturedArgumentValues) {
        return new LambdaExpression<>(resultType, parameterTypes, _body, this_, capturedArgumentValues);
    }
}