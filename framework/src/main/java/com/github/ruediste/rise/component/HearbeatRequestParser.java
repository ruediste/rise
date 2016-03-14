package com.github.ruediste.rise.component;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.core.RequestParser;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;

/**
 * Request parser for heartbeats sent by the pages in the browser. Updates the
 * heartbeat in the {@link PageHandle}s.
 * 
 * @see ComponentPageHandleRepository
 */
public class HearbeatRequestParser implements RequestParser {

    @Inject
    Logger log;

    @Inject
    ComponentPageHandleRepository componentPageRepository;

    public class HeartbeatParseResult implements RequestParseResult {

        private long pageNr;

        public HeartbeatParseResult(long pageNr) {
            this.pageNr = pageNr;
        }

        @Override
        public void handle() {
            log.trace("processing heartbeat for page {}", pageNr);
            componentPageRepository.heartbeat(pageNr);
        }

    }

    @Override
    public RequestParseResult parse(HttpRequest req) {
        long pageNr;
        try {
            pageNr = Long.parseLong(req.getParameter("nr"));
        } catch (Throwable t) {
            log.warn("no page number parameter sent along with heartbeat request");
            throw t;
        }
        return new HeartbeatParseResult(pageNr);
    }
}
