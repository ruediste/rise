package com.github.ruediste.rise.component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.component.components.DisplayStack;
import com.github.ruediste.rise.component.reload.PageReloadRequest;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.FragmentJumpPad;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.ICoreUtil;
import com.github.ruediste.rise.core.persistence.TransactionCallbackNoResult;
import com.github.ruediste.rise.core.persistence.TransactionControl;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.core.web.HttpRenderResult;

@Singleton
public class ComponentUtil implements ICoreUtil {

    @Inject
    private ComponentPage pageInfo;

    @Inject
    private ComponentConfiguration componentConfiguration;

    @Inject
    private CoreUtil coreUtil;

    @Inject
    private ComponentRequestInfo componentRequestInfo;

    @Inject
    private PageScopeManager pageScopeHandler;

    @Inject
    private PageReloadRequest reloadRequest;

    @Inject
    private EntityManagerHolder holder;

    @Inject
    private TransactionControl template;

    @Inject
    DisplayStack stack;

    public long pageId() {
        return pageInfo.getPageId();
    }

    public long getComponentNr(Component<?> fragment) {

        long result = FragmentJumpPad.getFragmentNr(fragment);
        if (result == -1) {
            // lazily set the Component numbers. This allows components to
            // reference each other without caring about
            // the rendering order.
            result = pageInfo.getNextFragmentNr();
            FragmentJumpPad.setFragmentNr(fragment, result);
            pageInfo.getFragmentNrMap().put(result, fragment);
        }
        return result;
    }

    public Component<?> getFragment(long fragmentId) {
        return pageInfo.getFragmentNrMap().get(fragmentId);
    }

    /**
     * Return the appropriate value for the html element id attribute.
     */
    public String getFragmentId(Component<?> fragment) {
        return "c_" + getComponentNr(fragment);
    }

    public String getReloadUrl() {
        return coreUtil.url(componentConfiguration.getReloadPath() + "/" + pageId());
    }

    public String getAjaxUrl(Component<?> component) {
        return coreUtil.url(componentConfiguration.getAjaxPath() + "/" + pageId() + "/" + getComponentNr(component));
    }

    @Override
    public CoreUtil getCoreUtil() {
        return coreUtil;
    }

    /**
     * Return a key for identifying a request parameter for the given fragment
     */
    public String getParameterKey(Component<?> fragment, String keySuffix) {
        return "c_" + getComponentNr(fragment) + "_" + keySuffix;
    }

    public Optional<Object> getParameterObject(Component<?> component, String keySuffix) {
        return reloadRequest.getParameterObject(getParameterKey(component, keySuffix));
    }

    /**
     * Return the value of a parameter belonging to a certain Component
     * <?> during a page reload request.
     * 
     * @param keySuffix
     *            value passed to {@link #getParameterKey(Component, String)} as
     *            suffix
     */
    public Optional<String> getParameterValue(Component<?> fragment, String keySuffix) {
        return reloadRequest.getParameterValue(getParameterKey(fragment, keySuffix));
    }

    public Collection<Object> getParameterObjects(Component<?> fragment, String keySuffix) {
        return reloadRequest.getParameterObjects(getParameterKey(fragment, keySuffix));
    }

    public List<String> getParameterValues(Component<?> fragment, String keySuffix) {
        return reloadRequest.getParameterValues(getParameterKey(fragment, keySuffix));
    }

    /**
     * Test if a parameter is defined for a certain Component<?> during a page
     * reload request.
     */
    public boolean isParameterDefined(Component<?> fragment, String key) {
        return reloadRequest.isParameterDefined(getParameterKey(fragment, key));
    }

    public void commit() {
        checkAndCommit(null, null);
    }

    public void commit(Runnable inTransaction) {
        checkAndCommit(null, inTransaction);
    }

    public void checkAndCommit(Runnable checker) {
        checkAndCommit(checker, null);
    }

    public void checkAndCommit(Runnable checker, Runnable inTransaction) {
        template.updating().execute(new TransactionCallbackNoResult() {

            @Override
            public void doInTransaction() {

                // run checker with separate EMs
                if (checker != null) {
                    template.forceNewEntityManagerSet().execute(checker::run);
                }

                holder.joinTransaction();

                if (inTransaction != null) {
                    inTransaction.run();
                }
            }
        });
    }

    /**
     * Instead of rendering this page again, close it an use the given result to
     * render the response.
     */
    public void closePage(HttpRenderResult closePageResult) {
        componentRequestInfo.setClosePageResult(closePageResult);
    }

    /**
     * Run the given runnable in the page scope of the current request. Mainly
     * useful in ajax request handling code. Note that only a single thread can
     * enter the scope of a page. Do not lock the page for extended periods of
     * time (like long running search queries)
     */
    public void runInPageScope(Runnable r) {
        PageHandle pageHandle = componentRequestInfo.getPageHandle();
        synchronized (pageHandle.lock) {
            pageScopeHandler.inScopeDo(pageHandle.pageScopeState, r);
        }
    }

    /**
     * push a runnable to the display stack
     */
    public Runnable push(Runnable run) {
        stack.getStack().push(run);
        return run;
    }

    /**
     * pop the top runnable from the display stack
     */
    public void pop() {
        stack.getStack().pop();
    }
}
