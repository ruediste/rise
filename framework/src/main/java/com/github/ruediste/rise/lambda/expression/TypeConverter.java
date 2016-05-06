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

package com.github.ruediste.rise.lambda.expression;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

final class TypeConverter  {

	// see http://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.1.2
	private static final Map<Class<?>, List<Class<?>>> primitiveWides;

	static {
		Map<Class<?>, List<Class<?>>> wides = new HashMap<>();
		wides.put(
				Byte.TYPE,
				Arrays.asList(new Class<?>[] { Short.TYPE, Integer.TYPE,
						Long.TYPE }));
		wides.put(Short.TYPE,
				Arrays.asList(new Class<?>[] { Integer.TYPE, Long.TYPE }));

		// wides.put(Character.TYPE,
		// Arrays.asList(new Class<?>[] { Integer.TYPE, Long.TYPE }));

		wides.put(Integer.TYPE, Arrays.asList(new Class<?>[] { Long.TYPE }));

		wides.put(Float.TYPE, Arrays.asList(new Class<?>[] { Double.TYPE }));

		primitiveWides = wides;
	}


	static Expression convert(Expression e, Class<?> to) {
		if (isAssignable(to, e.getResultType()))
			return e;

		return Expression.convert(e, to);
	}

	

	public static boolean isAssignable(Class<?> to, Class<?> from) {
		if (to.isAssignableFrom(from))
			return true;

		List<Class<?>> wides = primitiveWides.get(from);
		if (wides != null)
			return wides.contains(to);

		return false;

	}
}
