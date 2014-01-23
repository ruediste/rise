package sampleApp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/test")
public class TestServlet extends HttpServlet{

	private static final long serialVersionUID = -2306626660188818275L;

	@Inject
	TestBean testBean;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		testBean.test();
		
		
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("html");
		PrintWriter out = resp.getWriter();
		out.print("<html><head></head><body>Hello World"+testBean.load()+"</body></html>");
		out.close();
	}
}
