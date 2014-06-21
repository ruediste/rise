package laf.base;

public class Val<T> {

	private T value;

	public Val() {

	}

	public Val(T value) {
		this.value = value;
	}

	public static <T> Val<T> of(T value) {
		return new Val<>(value);
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}
}
