package com.github.ruediste.rise.nonReloadable.lambda.expression;

/**
 * Expression visitor returning null for all visit() methods.
 */
public class NullExpressionVisitor<T> implements ExpressionVisitor<T> {

    @Override
    public T visit(BinaryExpression e) {
        return null;
    }

    @Override
    public T visit(ThisExpression e) {
        return null;
    }

    @Override
    public T visit(ConstantExpression e) {
        return null;
    }

    @Override
    public T visit(MemberExpression e) {
        return null;
    }

    @Override
    public T visit(ParameterExpression e) {
        return null;
    }

    @Override
    public T visit(UnaryExpression e) {
        return null;
    }

    @Override
    public T visit(TenaryExpression tenaryExpression) {
        return null;
    }

    @Override
    public T visit(CapturedArgumentExpression capturedArgumentExpression) {
        return null;
    }

    @Override
    public T visit(GetLocalVariableExpression getLocalVariableExpression) {
        return null;
    }

}
