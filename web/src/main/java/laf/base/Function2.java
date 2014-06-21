package laf.base;

/**
 * Function from two parameters to a result
 *
 * @param <A>
 *            type of the first parameter
 * @param <B>
 *            type of the second parameter
 * @param <R>
 *            type of the result
 */
public interface Function2<A, B, R> {

	R apply(A a, B b);
}
