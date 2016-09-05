package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.integration.RiseCanvas;

/**
 * Renders a {@link Component} to HTML and processes updates from the view.
 *
 * <p>
 * {@link Component}s are view technology agnostic. The templates are used to
 * render a component and to parse results sent by the client. A template is
 * associated with each component, but a single template can well be shared
 * between components. The interface was designed to allow this.
 * </p>
 * 
 * <p>
 * Instances are held by the {@link ComponentTemplateIndex} and instantiated
 * only once.
 * </p>
 */
public interface IComponentTemplate<T extends Component<T>> {

    void doRender(T component, RiseCanvas<?> html);

    /**
     * Raise the events of this fragment only
     */
    default void applyValues(T component) {
        // NOP
    }

    /**
     * Process actions for this fragment
     */
    default void processActions(T component) {
        // NOP
    }

    /**
     * Handle an ajax request targeted at the given component.
     * 
     * <p>
     * To create the corresponding URL use
     * {@link HtmlFragmentBase#getAjaxUrl(Component)} . Anything you append to
     * the url (prefixed with a "/") will be available as suffix.
     * 
     * <p>
     * To handle the request you can either return a {@link HttpRenderResult} or
     * handle the request in the method by using
     * {@link CoreRequestInfo#getServletResponse()} and returning null
     * 
     * @return a {@link HttpRenderResult} which will be used to send the
     *         response, or null if the response has already be sent.
     * 
     */

    default HttpRenderResult handleAjaxRequest(T component, String suffix) throws Throwable {
        throw new UnsupportedOperationException();
    }

}
