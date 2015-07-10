package com.github.ruediste.rise.mvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.transaction.TransactionManager;

import com.github.ruediste.rise.api.ViewMvcBase;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.HttpService;
import com.github.ruediste.rise.core.ICoreUtil;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.web.ContentRenderResult;
import com.github.ruediste.rise.core.web.HttpServletResponseCustomizer;
import com.github.ruediste.rise.core.web.RedirectRenderResult;
import com.github.ruediste.salta.jsr330.Injector;

public class MvcUtil implements ICoreUtil {

    @Inject
    Provider<ActionInvocationBuilder> actionPathBuilderInstance;

    @Inject
    HttpService httpService;

    @Inject
    CoreUtil coreUtil;

    @Inject
    Injector injector;

    @Inject
    TransactionManager txm;

    @Inject
    MvcRequestInfo info;

    @Inject
    CoreConfiguration coreConfiguration;

    @Override
    public CoreUtil getCoreUtil() {
        return coreUtil;
    }

    public <TView extends ViewMvcBase<?, TData>, TData> ActionResult view(
            Class<TView> viewClass, TData data) {

        TView view = injector.getInstance(viewClass);
        view.initialize(data);

        ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
        try {
            view.render(stream);
        } catch (IOException e) {
            throw new RuntimeException("Error while rendering view", e);

        }
        return new ContentRenderResult(stream.toByteArray(),
                r -> {
                    r.setContentType(coreConfiguration.htmlContentType);
                    if (view instanceof HttpServletResponseCustomizer) {
                        ((HttpServletResponseCustomizer) view)
                                .customizeServletResponse(r);
                    }
                }

        );
    }

    public ActionResult redirect(ActionResult path) {
        return new RedirectRenderResult(coreUtil.toPathInfo(path));
    }

    /**
     * Commit the current transaction. After this method returns, the current
     * transaction is closed. Thus views should be created before calling this
     * method.
     */
    public void commit() {
        if (!info.isUpdating()) {
            throw new RuntimeException(
                    "Cannot commit from a method not annotated with @Updating");
        }

        info.getTransactionControl().commit();
    }
}
