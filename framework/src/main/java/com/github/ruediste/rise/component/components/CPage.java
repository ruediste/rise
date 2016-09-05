package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste1.i18n.lString.LString;

public class CPage extends Component<CPage> {
    private LString title;
    private Runnable body;

    public CPage(Runnable body) {
        this.body = body;
    }

    public LString getTitle() {
        return title;
    }

    public CPage setTitle(LString title) {
        this.title = title;
        return this;
    }

    public CPage body(Runnable body) {
        this.body = body;
        return this;
    }

    public Runnable body() {
        return body;
    }

}
