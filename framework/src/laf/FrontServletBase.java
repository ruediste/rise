package laf;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
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
public class FrontServletBase extends HttpServlet {

	@Inject
	Instance<Object> controllerInstance;

	@Inject
	BeanManager beanManager;

	@Inject
	ActionContext actionContext;

	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void initialize() {
		Controller controllerAnnotation = new Controller() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return Controller.class;
			}
		};

		for (Bean<?> bean : beanManager.getBeans(Object.class,
				controllerAnnotation)) {
			System.out.println(bean.getBeanClass());
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// parse request

		// call controller
		// SampleController controller =
		// controllerInstance.select(SampleController.class).get();
		// ActionResult result = controller.index();
		ActionResult result = new NormalRenderResult("Foo");

		// render result
		RenderResult renderResult = (RenderResult) result;
		renderResult.sendTo(resp);
	}
}
