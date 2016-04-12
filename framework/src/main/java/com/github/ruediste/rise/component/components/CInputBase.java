package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.RelationsComponent;

/**
 * Base class for input components. Provides a violationStatus and labels
 */
public class CInputBase<TSelf extends CInputBase<TSelf>> extends RelationsComponent<TSelf>
        implements LabeledComponentTrait<TSelf> {

    private boolean renderFormGroup = true;

    private boolean inline;

    private LabeledComponentStatus labeledComponentStatus = new LabeledComponentStatus();

    @Override
    public LabeledComponentStatus internal_getLabeledComponentStatus() {
        return labeledComponentStatus;
    }

    public boolean isRenderFormGroup() {
        return renderFormGroup;
    }

    public TSelf setRenderFormGroup(boolean renderFormGroup) {
        this.renderFormGroup = renderFormGroup;
        return self();
    }

    public boolean isInline() {
        return inline;
    }

    public TSelf setInline(boolean inline) {
        this.inline = inline;
        return self();
    }

    public TSelf inline() {
        this.inline = true;
        return self();
    }

}
