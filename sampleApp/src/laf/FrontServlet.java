package laf;

import java.io.IOException;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Framework entry point
 */
@WebServlet("/front")
public class FrontServlet extends HttpServlet {


	@Inject
	Instance<Object> controllerInstance;
	
	@Inject
	ActionContext actionContext;
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// parse request

		// call controller
		SampleController controller = controllerInstance.select(SampleController.class).get();
		controller.index();
		
		// render result
		RenderResult renderResult = actionContext.getRenderResult();
		renderResult.sendTo(resp);
	}
}
