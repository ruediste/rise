package laf;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class ActionContext {

	private RenderResult renderResult;

	public RenderResult getRenderResult() {
		return renderResult;
	}

	public void setRenderResult(RenderResult renderResult) {
		this.renderResult = renderResult;
	}
}
