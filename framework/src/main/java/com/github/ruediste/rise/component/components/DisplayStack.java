package com.github.ruediste.rise.component.components;

import java.util.ArrayDeque;

import com.github.ruediste.rise.component.PageScoped;

@PageScoped
public class DisplayStack {
    private final ArrayDeque<Runnable> stack = new ArrayDeque<>();

    public ArrayDeque<Runnable> getStack() {
        return stack;
    }
}
