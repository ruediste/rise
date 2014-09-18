package laf.core.base;

/**
 * Consumer of two parameters
 *
 * @param <A>
 *            type of the first parameter
 * @param <B>
 *            type of the second parameter
 */
public interface Consumer2<A, B> extends ThrowingConsumer2<A, B> {

	@Override
	void accept(A a, B b);

	public static <A, B> Consumer2<A, B> nonThrowing(
			ThrowingConsumer2<A, B> wrapped) {
		return (a, b) -> {
			try {
				wrapped.accept(a, b);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
}
