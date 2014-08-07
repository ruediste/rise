package laf.core.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class ContentRenderResult implements HttpRenderResult {
	public final byte[] content;

	public ContentRenderResult(byte[] content) {
		this.content = content;
	}

	public ContentRenderResult(String string) {
		try {
			content = string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void sendTo(HttpServletResponse response, HttpRenderResultUtil util)
			throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		ServletOutputStream out = response.getOutputStream();
		out.write(content);
		out.close();
	}
}
