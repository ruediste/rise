package com.github.ruediste.rise.component.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.github.ruediste.rise.component.fragment.HtmlFragment.UpdateStructureArg;

public class HtmlFragmentUtil {

    private HtmlFragmentUtil() {
    }

    public static List<HtmlFragment> subTree(HtmlFragment fragment) {
        ArrayList<HtmlFragment> result = new ArrayList<>();
        subTree(result, fragment);
        return result;
    }

    private static void subTree(ArrayList<HtmlFragment> result, HtmlFragment fragment) {
        result.add(fragment);
        fragment.getChildren().forEach(child -> subTree(result, child));
    }

    public static void forSubTree(HtmlFragment fragment, Consumer<HtmlFragment> consumer) {
        consumer.accept(fragment);
        fragment.getChildren().forEach(x -> forSubTree(x, consumer));
    }

    private static class UpdateStructureArgImpl implements UpdateStructureArg {

        boolean structureUpdated;
        boolean callOnFurtherStructureUpdates;

        @Override
        public void structureUpdated() {
            structureUpdated = true;
        }

        @Override
        public void callOnFurtherStructureUpdates() {
            callOnFurtherStructureUpdates = true;
        }

        public void reset() {
            structureUpdated = false;
            callOnFurtherStructureUpdates = false;
        }

    }

    public static void updateStructure(HtmlFragment root) {
        List<HtmlFragment> fragments = subTree(root);
        UpdateStructureArgImpl arg = new UpdateStructureArgImpl();

        while (true) {
            ArrayList<HtmlFragment> newFragments = new ArrayList<>();
            boolean updated = false;
            for (HtmlFragment fragment : fragments) {
                arg.reset();
                fragment.updateStructure(arg);
                if (arg.callOnFurtherStructureUpdates) {
                    newFragments.add(fragment);
                }
                updated |= arg.structureUpdated;
            }

            if (!updated)
                break;

            fragments = newFragments;
            if (newFragments.isEmpty())
                return;
        }
    }
}
