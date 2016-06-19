package com.github.ruediste.rise.component.fragment;

import java.util.List;

import com.github.ruediste.rise.component.fragment.HtmlFragment.UpdateStructureArg;

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

    public static void updateStructure(HtmlFragment root) {
        UpdateStructureArgImpl arg = new UpdateStructureArgImpl();

        while (true) {
            List<HtmlFragment> fragments = root.subTree();
            boolean updated = false;
            for (HtmlFragment fragment : fragments) {
                arg.reset();
                fragment.updateStructure(arg);
                updated |= arg.structureUpdated;
            }

            if (!updated)
                break;

        }
    }

}
