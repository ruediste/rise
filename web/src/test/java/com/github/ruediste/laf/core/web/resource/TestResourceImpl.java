package com.github.ruediste.laf.core.web.resource;

import java.io.UnsupportedEncodingException;

import com.github.ruediste.laf.core.web.resource.DataEqualityTracker;
import com.github.ruediste.laf.core.web.resource.Resource;

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

	public TestResourceImpl(String name, String data) {
		super();
		this.name = name;
		try {
			this.data = data.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
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