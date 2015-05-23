package com.github.ruediste.rise.component;

import java.util.Map;

import com.github.ruediste.salta.core.Binding;

public class PageHandle {
    public Map<Binding, Object> instances;

    /**
     * Lock used to guarantee single threaded access to a page
     */
    public final Object lock = new Object();

    public long id;
}
