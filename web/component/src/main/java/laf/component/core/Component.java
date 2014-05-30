package laf.component.core;

import java.io.IOException;

import laf.attachedProperties.AttachedPropertyBearer;
import laf.component.html.ApplyValuesUtil;
import laf.component.html.RenderUtil;
import laf.component.html.template.RaiseEventsUtil;

import org.rendersnake.HtmlCanvas;

/**
 * Interface of all components
 */
public interface Component extends AttachedPropertyBearer {

	/**
	 * Get the children of this component
	 */
	Iterable<Component> getChildren();

	/**
	 * Get the parent of this component. Can be null
	 */
	Component getParent();

	/**
	 * Notify this component that it's parent has been changed. When this method
	 * is called, this component will already be returned from
	 * {@link #getChildren()} of the new parent. It is the responsibility of the
	 * caller to ensure that {@link #childRemoved(Component)} is called on the
	 * old parent.
	 */
	void parentChanged(Component newParent);

	/**
	 * Notify this Component that the child has been removed. It is the
	 * responsibility of the caller to call {@link #parentChanged(Component)} on
	 * the child.
	 */
	void childRemoved(Component child);

	/**
	 * Called on the root component before rendering a page for the first time.
	 * The implementation of this method has to call the {@link #initialize()}
	 * method of all child components.
	 */
	void initialize();

	/**
	 * Render the component to the given {@link HtmlCanvas}
	 *
	 * @param html
	 * @throws IOException
	 */
	void render(HtmlCanvas html, RenderUtil util) throws IOException;

	void applyValues(ApplyValuesUtil util);

	void raiseEvents(RaiseEventsUtil util);

	Integer getComponentId();

	void setComponentId(Integer id);
}
