package com.github.ruediste.rise.component.render;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rise.integration.RiseCanvas;

public abstract class CanvasTargetForAttributePlaceholder implements HtmlCanvasTarget, RiseCanvasTarget {

    private void fail() {
        throw new RuntimeException("Cannot write tags from attribute placeholders");
    }

    @Override
    public void addAttributePlaceholder(RiseCanvas<?> html, Runnable placeholder) {
        throw new RuntimeException("Cannot add placeholder in attribute placeholder");
    }

    @Override
    public void addPlaceholder(RiseCanvas<?> html, Runnable placeholder) {
        throw new RuntimeException("Cannot add placeholder in attribute placeholder");
    }

    protected abstract void write(String str);

    @Override
    public void writeUnescapedWithoutAttributeCommitting(String str) {
        write(str);
    }

    @Override
    public void writeUnescaped(String str) {
        fail();

    }

    @Override
    public void tagStartedWithoutEndTag(String postAttributesFragment) {
        fail();

    }

    @Override
    public void tagStarted(String display, String postAttributesFragment, String closeFragment) {
        fail();

    }

    @Override
    public void commitAttributes() {
        fail();

    }

    @Override
    public void CLASS(String class_) {
        fail();

    }

    @Override
    public void checkAttributesUncommited() {
    }

    @Override
    public void close() {
        fail();

    }

    @Override
    public void close(String expectedDisplay) {
        fail();

    }

    @Override
    public void flush() {
        fail();

    }

}
