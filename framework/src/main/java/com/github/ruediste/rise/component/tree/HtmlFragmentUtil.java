package com.github.ruediste.rise.component.tree;

import java.util.List;

import com.github.ruediste.rise.component.tree.Component.UpdateStructureArg;

public class HtmlFragmentUtil {

    private HtmlFragmentUtil() {
    }

    private static class UpdateStructureArgImpl implements UpdateStructureArg {

        boolean structureUpdated;

        @Override
        public void structureUpdated() {
            structureUpdated = true;
        }

        public void reset() {
            structureUpdated = false;
        }

    }

    public static void updateStructure(Component root) {
        UpdateStructureArgImpl arg = new UpdateStructureArgImpl();

        while (true) {
            List<Component> fragments = root.subTree();
            boolean updated = false;
            for (Component fragment : fragments) {
                arg.reset();
                fragment.updateStructure(arg);
                updated |= arg.structureUpdated;
            }

            if (!updated)
                break;

        }
    }

}
