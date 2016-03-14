package com.github.ruediste.rise.nonReloadable.front;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ruediste.rise.util.Pair;
import com.google.common.base.Stopwatch;

/**
 * Logger for startup times.
 */
public class StartupTimeLogger {

    private static final Logger log = LoggerFactory.getLogger(StartupTimeLogger.class);
    private static final ArrayList<Pair<String, Stopwatch>> logs = new ArrayList<>();

    public static void stopAndLog(String title, Stopwatch watch) {
        watch.stop();
        logs.add(Pair.of(title, watch));
    }

    public static Iterable<Pair<String, Stopwatch>> getLogs() {
        return logs;
    }

    public static void clear() {
        logs.clear();
    }

    public static void writeTimesToLog(String title) {
        if (!log.isInfoEnabled())
            return;
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append("\n");
        for (Pair<String, Stopwatch> entry : logs) {
            sb.append(entry.getA());
            sb.append(": ");
            sb.append(entry.getB());
            sb.append("\n");
        }
        log.info(sb.toString());
    }

}
