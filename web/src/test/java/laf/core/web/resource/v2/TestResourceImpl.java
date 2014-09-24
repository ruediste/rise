package laf.core.web.resource.v2;

import java.io.UnsupportedEncodingException;

class TestResourceImpl implements Resource {

	private static class TestDET implements DataEqualityTracker {
		private byte[] data;

		public TestDET(byte[] data) {
			this.data = data;
		}

		@Override
		public boolean containsSameDataAs(DataEqualityTracker other) {
			if (getClass() != other.getClass()) {
				return false;
			}
			return data == ((TestDET) other).data;
		}
	}

	String name;
	byte[] data;

	public TestResourceImpl(String name, String data)
			throws UnsupportedEncodingException {
		super();
		this.name = name;
		this.data = data.getBytes("UTF-8");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public byte[] getData() {
		return data;
	}

	@Override
	public DataEqualityTracker getDataEqualityTracker() {
		return new TestDET(data);
	}

}