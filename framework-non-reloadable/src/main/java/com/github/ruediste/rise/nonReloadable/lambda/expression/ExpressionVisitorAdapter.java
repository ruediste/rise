package com.github.ruediste.rise.nonReloadable.lambda.expression;

/**
 * Expression visitor invoking {@link #visit(Expression)} for all visit()
 * methods
 */
public class ExpressionVisitorAdapter<T> implements ExpressionVisitor<T> {

    public T visit(Expression e) {
        return null;
    }

    @Override
    public T visit(BinaryExpression e) {
        return visit((Expression) e);
    }

    @Override
    public T visit(ThisExpression e) {
        return visit((Expression) e);
    }

    @Override
    public T visit(ConstantExpression e) {
        return visit((Expression) e);
    }

    @Override
    public T visit(MemberExpression e) {
        return visit((Expression) e);
    }

    @Override
    public T visit(ParameterExpression e) {
        return visit((Expression) e);
    }

    @Override
    public T visit(UnaryExpression e) {
        return visit((Expression) e);
    }

    @Override
    public T visit(TenaryExpression e) {
        return visit((Expression) e);
    }

    @Override
    public T visit(CapturedArgumentExpression e) {
        return visit((Expression) e);
    }

    @Override
    public T visit(GetLocalVariableExpression e) {
        return visit((Expression) e);
    }

}
