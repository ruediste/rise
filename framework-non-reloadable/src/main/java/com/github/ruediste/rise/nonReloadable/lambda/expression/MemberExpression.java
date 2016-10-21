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

import static java.util.stream.Collectors.joining;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.List;
import java.util.Objects;

/**
 * Represents accessing a field or method.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

public final class MemberExpression extends InvocationExpression {

	private final Member _member;
	private MemberExpressionType expressionType;

	MemberExpression(MemberExpressionType expressionType, Expression instance, Member member, Class<?> resultType,
			List<Class<?>> parameterTypes, List<Expression> arguments) {
		super(0, instance, resultType, parameterTypes, arguments);
		this.expressionType = expressionType;

		if (member instanceof AccessibleObject) {
			AccessibleObject ao = (AccessibleObject) member;
			if (!ao.isAccessible())
				ao.setAccessible(true);
		}

		_member = member;
	}

	/**
	 * Gets the {@link Member} to be accessed.
	 * 
	 * @return {@link Member} to be accessed.
	 */
	public final Member getMember() {
		return _member;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public String toString() {
		Member m = getMember();
		String result = getInstance() != null ? getInstance().toString() : m.getDeclaringClass().getName();
		result += "." + (m instanceof Constructor<?> ? "<new>" : m.getName());
		if (expressionType != MemberExpressionType.FieldAccess)
			result += "(" + getArguments().stream().map(Object::toString).collect(joining(", ")) + ")";
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_member == null) ? 0 : _member.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof MemberExpression))
			return false;
		final MemberExpression other = (MemberExpression) obj;

		return Objects.equals(_member, other._member) && Objects.equals(expressionType, other.expressionType);
	}

	public MemberExpressionType getExpressionType() {
		return expressionType;
	}
}