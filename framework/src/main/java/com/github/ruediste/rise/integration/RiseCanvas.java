package com.github.ruediste.rise.integration;

import com.github.ruediste.rendersnakeXT.canvas.FuncCanvas;
import com.github.ruediste.rendersnakeXT.canvas.Html5Canvas;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.assetPipeline.DefaultAssetTypes;
import com.github.ruediste1.i18n.lString.LString;

public interface RiseCanvas<TSelf extends RiseCanvas<TSelf>> extends
        Html5Canvas<TSelf>, FuncCanvas<TSelf> {

    RiseCanvasHelper internal_riseHelper();

    /**
     * Render a css link for all {@link DefaultAssetTypes#CSS} assets in the
     * given output
     */
    default TSelf rCssLinks(AssetBundleOutput output) {
        internal_riseHelper().rCssLinks(this, output);
        return self();
    }

    /**
     * Render a js link for all {@link DefaultAssetTypes#JS} assets in the given
     * output
     */
    default TSelf rJsLinks(AssetBundleOutput output) {
        internal_riseHelper().rJsLinks(this, output);
        return self();
    }

    default TSelf HREF(ActionResult destination) {
        return HREF(internal_riseHelper().getUtil().url(destination));
    }

    default TSelf HREF(PathInfo destination) {
        return HREF(internal_riseHelper().getUtil().url(destination));
    }

    default TSelf ACTION(ActionResult destination) {
        return ACTION(internal_riseHelper().getUtil().url(destination));
    }

    default TSelf ACTION(PathInfo destination) {
        return ACTION(internal_riseHelper().getUtil().url(destination));
    }

    default TSelf add(Component c) {
        internal_riseHelper().add(c);
        return self();
    }

    /**
     * Write the supplied buffer directly to the output
     */
    default TSelf writeRaw(byte[] buffer) {
        internal_riseHelper().writeRaw(buffer);
        return self();
    }

    default TSelf content(LString value) {
        return content(value.resolve(internal_riseHelper().getCurrentLocale()));
    }

    default TSelf write(LString value) {
        return write(value.resolve(internal_riseHelper().getCurrentLocale()));
    }

    default TSelf render(Component c) {
        internal_riseHelper().renderComponent(c, this);
        return self();
    }

    default TSelf renderChildren(Component parent) {
        for (Component c : parent.getChildren())
            internal_riseHelper().renderComponent(c, this);
        return self();
    }
}
