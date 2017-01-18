package com.github.ruediste.rise.component;

import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.scopes.HttpScopeManager;

/**
 * Manager of a timer cleaning up {@link ComponentPage}s which do not receive a
 * heartbeat anymore.
 * 
 * @see HearbeatRequestParser
 * @see ComponentPageHandleRepository
 */
@Singleton
public class PageScopeCleaner {

    @Inject
    Logger log;

    @Inject
    HttpScopeManager mgr;

    @Inject
    PageScopeManager pageScopeManager;

    @Inject
    ComponentPageHandleRepository repo;

    @Inject
    ComponentPage page;

    private Timer timer;

    public void start() {
        timer = new Timer("pageScopeCleanupThread", true);

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                mgr.runInEachSessionScope(() -> {
                    for (PageHandle handle : repo.getPageHandles()) {
                        Instant now = Instant.now();
                        if (handle.getEndOfLife().isAfter(now))
                            continue;

                        try {
                            synchronized (handle.lock) {
                                if (handle.getEndOfLife().isAfter(now))
                                    continue;

                                pageScopeManager.inScopeDo(handle.pageScopeState, () -> {
                                    page.destroy();
                                });
                            }
                        } catch (Throwable t) {
                            log.error("Error in page scope cleanup thread", t);
                        }
                    }
                });
            }
        }, 0L, Duration.ofSeconds(1).toMillis());
    }

    public void stop() {
        timer.cancel();
    }

}
