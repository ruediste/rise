package com.github.ruediste.rise.component;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.component.reload.ReloadHandler;
import com.github.ruediste.rise.core.scopes.HttpScopeManager;
import com.github.ruediste.rise.core.scopes.SessionScopeEvents;
import com.github.ruediste.rise.core.scopes.SessionScoped;
import com.github.ruediste.salta.standard.util.SimpleScopeManagerBase.ScopeState;

/**
 * References all pages in a session.
 * 
 * <p>
 * <b> Life Cycle </b> <br>
 * The {@link ComponentPage#getDestroyEvent()} is fired when a page is
 * destroyed. It can be triggered by
 * <ul>
 * <li>leaving the page via a redirect</li>
 * <li>destruction of the containing session</li>
 * <li>missing heartbeat, typically due to the user closing the browser or the
 * user navigating away from the page via a link</li>
 * </ul>
 * Page destruction can be triggered programmatically using
 * {@link #destroyCurrentPage()}, which is what the {@link ReloadHandler} does
 * for a redirect.
 * <p>
 * During construction, the {@link ComponentPageHandleRepository} registers
 * itself with {@link SessionScopeEvents#getScopeDestroyedEvent()} to destroy
 * all pages if the session is destroyed.
 * <p>
 * Finally, the {@link PageScopeCleaner} periodically checks all page scopes for
 * missing heartbeats. The heartbeats are sent by a JavaScript to the server,
 * where they are procssed in the {@link HearbeatRequestParser}, which invokes
 * {@link #heartbeat(long)}.
 */
@SessionScoped
public class ComponentPageHandleRepository {

    @Inject
    Logger log;

    @Inject
    ComponentConfiguration config;

    @Inject
    ComponentPage page;

    @Inject
    HttpScopeManager httpScopeManager;

    @Inject
    PageScopeManager pageScopeManager;

    private AtomicLong nextPageId = new AtomicLong();

    ConcurrentHashMap<Long, PageHandle> pageHandles = new ConcurrentHashMap<>();

    @PostConstruct
    public void postConstruct(SessionScopeEvents events) {
        events.getScopeDestroyedEvent().addListener(session -> {
            for (PageHandle handle : pageHandles.values()) {
                synchronized (handle.lock) {
                    ScopeState old = pageScopeManager.setState(handle.pageScopeState);
                    try {
                        destroyCurrentPage();
                    } catch (Throwable e) {
                        log.error("Error while destroying page", e);
                    } finally {
                        pageScopeManager.setState(old);
                    }

                }
            }
        });
    }

    public PageHandle createPageHandle() {
        PageHandle handle = new PageHandle();
        handle.id = nextPageId.getAndIncrement();
        handle.setEndOfLife(x -> Instant.now().plus(config.getHeartbeatInterval()));
        pageHandles.put(handle.id, handle);
        return handle;
    }

    public PageHandle getPageHandle(long id) {
        return pageHandles.get(id);
    }

    /**
     * Destroy the current page. The page scope has to be entered before calling
     * this method
     */
    public void destroyCurrentPage() {
        page.destroy();
        pageHandles.remove(page.getPageId());
    }

    public Collection<PageHandle> getPageHandles() {
        return pageHandles.values();
    }

    public void heartbeat(long pageNr) {
        PageHandle handle = pageHandles.get(pageNr);
        if (handle != null)
            handle.setEndOfLife(x -> {
                Instant newEOL = Instant.now().plus(config.getHeartbeatInterval());
                return newEOL.isAfter(x) ? newEOL : x;
            });
    }
}
