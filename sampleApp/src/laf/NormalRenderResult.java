package laf;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class NormalRenderResult implements RenderResult {
	public final String string;

	public NormalRenderResult(String string) {
		this.string = string;
	}

	@Override
	public void sendTo(HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter writer = response.getWriter();
		writer.write(string);
		writer.flush();
	}
}
