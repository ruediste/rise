package laf.component.basic;


public class CButton extends MultiChildrenComponent<CButton> {
	private Runnable handler;

	public CButton() {
	}

	public CButton(String text) {
		children.add(new CText(text));
	}

	CButton withHandler(Runnable handler) {
		this.handler = handler;
		return this;
	}

	public Runnable getHandler() {
		return handler;
	}

}
