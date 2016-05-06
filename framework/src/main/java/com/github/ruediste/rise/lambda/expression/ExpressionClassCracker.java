/*
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

package com.github.ruediste.rise.lambda.expression;

import static java.util.stream.Collectors.toList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.github.ruediste.rise.lambda.LambdaExpression;

class ExpressionClassCracker {

	/**
	 * 
	 * @param lambdaInterfaceMethod
	 *            implemented mehtod of the lambda interface
	 * @param lambdaImpl
	 *            method implementing the lambda expression
	 * @param capturedArgs
	 *            captured arguments, including leading this reference if
	 *            applicable
	 */
	<T> LambdaExpression<T> parseLambdaMethod(MethodType samMethodType, Member lambdaImpl, Object[] capturedArgs) {
		boolean isStatic = (lambdaImpl.getModifiers() & Modifier.STATIC) != 0;

		Method lambdaImplMethod = (Method) lambdaImpl;
		if (!lambdaImpl.isSynthetic()) {
			// this means we're dealing with a method reference

			// As values we have the captured args followed by the parameters.
			// They have to be mapped
			// to the this reference followed by the arguments of the
			// implementation method

			ArrayList<Expression> values = new ArrayList<>();
			values.addAll(Arrays.stream(capturedArgs).map(x -> new CapturedArgumentExpression(getClass(), 0))
					.collect(toList()));
			// values.addAll(new ParameterExpression(getClass(), 0));

			Expression instanceExpression = null;
			Object instanceValue = null;
			ArrayList<Expression> parameterExpressions = new ArrayList<>();
			boolean thisProcessed = false;
			int paramterIndex = 0;

			// process captured arguments
			for (int idx = 0; idx < capturedArgs.length; idx++) {
				if (!thisProcessed && !isStatic) {
					// add captured argument as this expression
					instanceExpression = new ThisExpression(lambdaImpl.getDeclaringClass());
					instanceValue = capturedArgs[0];
				} else {
					// add parameter
					parameterExpressions.add(
							new CapturedArgumentExpression(lambdaImplMethod.getParameterTypes()[paramterIndex++], idx));
				}
				thisProcessed = true;
			}

			// process lambda arguments
			for (int idx = 0; idx < samMethodType.parameterCount(); idx++) {
				if (!thisProcessed && !isStatic) {
					// add captured argument as this expression
					instanceExpression = new ParameterExpression(lambdaImpl.getDeclaringClass(), idx);
				} else {
					// add parameter
					parameterExpressions
							.add(new ParameterExpression(lambdaImplMethod.getParameterTypes()[paramterIndex++], idx));
				}
				thisProcessed = true;
			}

			MemberExpression methodInvocation = new MemberExpression(MemberExpressionType.MethodAccess,
					instanceExpression, lambdaImpl, lambdaImplMethod.getReturnType(),
					Arrays.asList(lambdaImplMethod.getParameterTypes()), parameterExpressions);

			return new LambdaExpression<>(samMethodType.returnType(), samMethodType.parameterArray(), methodInvocation,
					instanceValue, new Object[] {});
		}
		// parse the method
		ExpressionClassVisitor visitor = parseClass(lambdaImpl.getDeclaringClass().getClassLoader(),
				lambdaImpl.getDeclaringClass().getName().replace('.', '/') + ".class", lambdaImplMethod);

		// bind the local variable references to the this reference, captured
		// arguments and parameters
		Expression expr;
		{
			int firstCapturedArgIndex = isStatic ? 0 : 1;
			int firstParameterIndex = firstCapturedArgIndex + capturedArgs.length;

			expr = visitor.getResult().accept(new CopyExpressionVisitor() {
				@Override
				public Expression visit(GetLocalVariableExpression e) {
					if (e.getIndex() >= firstParameterIndex) {
						return new ParameterExpression(e.getResultType(), e.getIndex() - firstParameterIndex);
					}
					if (e.getIndex() >= firstCapturedArgIndex)
						return new CapturedArgumentExpression(e.getResultType(), e.getIndex() - firstCapturedArgIndex);
					return new ThisExpression(e.getResultType());
				}
			});
		}

		// hack: cast booleans if necessary, since they are parsed as integers
		expr = Expression.convert(expr, samMethodType.returnType());

		return new LambdaExpression<T>(samMethodType.returnType(), samMethodType.parameterArray(), expr,
				isStatic ? null : capturedArgs[0],
				isStatic ? capturedArgs : Arrays.copyOfRange(capturedArgs, 1, capturedArgs.length));

	}

	private ExpressionClassVisitor parseClass(ClassLoader classLoader, String classFilePath, Method method) {
		return parseClass(classLoader, classFilePath, method.getName(), Type.getMethodDescriptor(method));
	}

	private ExpressionClassVisitor parseClass(ClassLoader classLoader, String classFilePath, String method,
			String methodDescriptor) {
		ExpressionClassVisitor visitor = new ExpressionClassVisitor(method, methodDescriptor, classLoader);
		try {
			try (InputStream classStream = getResourceAsStream(classLoader, classFilePath)) {
				ClassReader reader = new ClassReader(classStream);
				reader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
				return visitor;
			}
		} catch (IOException e) {
			throw new RuntimeException("error parsing class file " + classFilePath, e);
		}
	}

	private InputStream getResourceAsStream(ClassLoader classLoader, String path) throws FileNotFoundException {
		InputStream stream = classLoader.getResourceAsStream(path);
		if (stream == null)
			throw new FileNotFoundException(path);
		return stream;
	}

}
