package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.RelationsComponent;

/**
 * Display an Image which can come from various data source
 */
public class CImg extends RelationsComponent<CImg> {

    Supplier<byte[]> source;

    String jpegMimeType = "image/jpeg";
}
