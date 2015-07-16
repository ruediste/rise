package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.RelationsComponent;

/**
 * Display an Image which can come from various data source
 */
@DefaultTemplate(CImgTemplate.class)
public class CImg extends RelationsComponent<CImg> {

    private Supplier<byte[]> source;

    private String alt;

    public String getAlt() {
        return alt;
    }

    public CImg setAlt(String alt) {
        this.alt = alt;
        return this;
    }

    public Supplier<byte[]> getSource() {
        return source;
    }

    public CImg setSource(Supplier<byte[]> source) {
        this.source = source;
        return this;
    }
}
