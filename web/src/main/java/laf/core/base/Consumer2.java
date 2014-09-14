package laf.core.base;

/**
 * Consumer of two parameters
 *
 * @param <A>
 *            type of the first parameter
 * @param <B>
 *            type of the second parameter
 */
public interface Consumer2<A, B> {

	void accept(A a, B b);
}
