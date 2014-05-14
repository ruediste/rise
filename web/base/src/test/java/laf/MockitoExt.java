package laf;

import org.mockito.Mockito;

import com.google.common.reflect.TypeToken;

public class MockitoExt extends Mockito {
	@SuppressWarnings("unchecked")
	public static <T> T mock(TypeToken<T> type) {
		return (T) Mockito.mock(type.getRawType());
	}
}
