package com.github.ruediste.rise.sample;

import com.github.ruediste.rise.api.ViewMvcBase;
import com.github.ruediste.rise.mvc.IControllerMvc;

/**
 * Base class for views for the MVC framework.
 */
public abstract class ViewMvc<TController extends IControllerMvc, TData>
        extends ViewMvcBase<TController, TData, SampleCanvas> {

}
