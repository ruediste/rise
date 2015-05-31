package com.github.ruediste.rise.integration;

import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvas;
import com.github.ruediste.rendersnakeXT.canvas.FuncCanvas;

public interface BootstrapRiseCanvas<TSelf extends BootstrapRiseCanvas<TSelf>>
        extends BootstrapCanvas<TSelf>, RiseCanvas<TSelf>, FuncCanvas<TSelf> {

}
