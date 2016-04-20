package com.github.ruediste.rise.component;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.Labeled;

/**
 * Class containing methods for default actions
 */
public class DefaultActions {

    @Labeled
    @GlyphiconIcon(Glyphicon.plus)
    public void add(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.minus)
    public void remove(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.edit)
    public void chooseItems(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.arrow_left)
    public void back(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.edit)
    public void pick(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.remove)
    public void clear(Runnable callback) {
        callback.run();
    }

}
