package com.github.ruediste.rise.sample.fileupload;

import com.github.ruediste.rise.component.components.DefaultTemplate;
import com.github.ruediste.rise.component.tree.RelationsComponent;

@DefaultTemplate(CViewerJSTemplate.class)
public class CViewerJS extends RelationsComponent<CViewerJS> {

    private byte[] source;

    public byte[] getSource() {
        return source;
    }

    public CViewerJS setSource(byte[] source) {
        this.source = source;
        return this;
    }
}
