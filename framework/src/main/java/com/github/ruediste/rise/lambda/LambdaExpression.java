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

package com.github.ruediste.rise.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.github.ruediste.rise.lambda.expression.CapturedArgumentExpression;
import com.github.ruediste.rise.lambda.expression.Expression;
import com.github.ruediste.rise.lambda.expression.LambdaExpressionCompiler;
import com.github.ruediste.rise.lambda.expression.LambdaInformationWeaver;
import com.github.ruediste.rise.lambda.expression.ParameterExpression;
import com.github.ruediste.rise.lambda.expression.ThisExpression;

/**
 * Describes a lambda expression consisting of
 * <ul>
 * <li>an expression representing the code of the lambda</li>
 * <li>values for captured arguments</li>
 * <li>a this instance, if applicable</li>
 * <li>the lambda interface and the method beeing implemented, defining
 * parameter types and return type</li>
 * </ul>
 * Please note that this is not an {@link Expression} itself.
 * <p>
 * The body expression contains a {@link ThisExpression} for references to the
 * this instance, {@link CapturedArgumentExpression}s to reference captured
 * arguments, which can be resolved using
 * {@link #getValue(CapturedArgumentExpression)} and {@link ParameterExpression}
 * s for the parameters of the lambda expression.
 * <p>
 * Use {@link #parse(Object)} method to get a lambda expression tree.
 * </p>
 * 
 * @param <F>
 *            type of the lambda represented by this LambdaExpression.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

public final class LambdaExpression<F> {

    private final Expression _body;
    private final Object this_;
    private final Object[] capturedArgumentValues;
    private Class<?> resultType;
    private Class<?>[] parameterTypes;

    public LambdaExpression(Class<?> resultType, Class<?>[] parameterTypes, Expression _body, Object this_,
            Object[] capturedArgumentValues) {
        super();
        this.resultType = resultType;
        this.parameterTypes = parameterTypes;
        this._body = _body;
        this.this_ = this_;
        this.capturedArgumentValues = capturedArgumentValues;
    }

    /**
     * Gets the body of the lambda expression.
     * 
     * @return {@link Expression}
     */
    public Expression getBody() {
        return _body;
    }

    @SuppressWarnings("unchecked")
    public static <T> LambdaExpression<T> parse(Object lambda) {
        return (LambdaExpression<T>) LambdaInformationWeaver.getLambdaExpression(lambda);

    }

    /**
     * Produces a {@link Function} that represents the lambda expression.
     * 
     * @return {@link Function} that represents the lambda expression.
     */
    public Function<Object[], ?> compile() {
        final Function<Object[], ?> f = _body.accept(new LambdaExpressionCompiler(this));
        return f;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append('(');
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                b.append(',');
                b.append(' ');
            }
            b.append(parameterTypes[i].getName());
            b.append(" P");
            b.append(i);
        }
        b.append(')');
        b.append("->");
        b.append('{');
        b.append(getBody().toString());
        b.append('}');
        return b.toString();
    }

    public List<Class<?>> getParamTypes() {
        return Arrays.asList(parameterTypes);
    }

    public Class<?> getReturnType() {
        return resultType;
    }

    public Object getValue(CapturedArgumentExpression e) {
        return capturedArgumentValues[e.getIndex()];
    }

    public Object getThis() {
        return this_;
    }
}
