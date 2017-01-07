package com.github.ruediste.rise.core.navigation;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Html5Canvas;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.navigation.Navigation.NavigationItem;
import com.github.ruediste.rise.integration.IconUtil;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;

public class NavigationBuilder {

    private final class SameActionInvocationPredicate implements Predicate<ActionInvocation<Object>> {
        private ActionInvocation<?> referenceInvocation;

        public SameActionInvocationPredicate(ActionInvocation<?> referenceInvocation) {
            this.referenceInvocation = referenceInvocation;
        }

        @Override
        public boolean test(ActionInvocation<Object> x) {
            return x.methodInvocation.getMethod().equals(referenceInvocation.methodInvocation.getMethod());
        }
    }

    @Inject
    CoreUtil util;

    @Inject
    LabelUtil labelUtil;

    @Inject
    IconUtil iconUtil;

    private final Navigation result = new Navigation();

    private Deque<NavigationItem> currentGroup = new ArrayDeque<>();

    /**
     * Add a navigation item to the current group
     */
    public NavigationBuilder add(NavigationItem item) {
        if (currentGroup.isEmpty())
            result.getRootItems().add(item);
        else
            currentGroup.peek().children.add(item);
        return this;
    }

    protected NavigationItem lastItem() {
        List<NavigationItem> list;
        if (currentGroup.isEmpty())
            list = result.getRootItems();
        else
            list = currentGroup.peek().children;
        return list.get(list.size() - 1);
    }

    public NavigationBuilder add(ActionResult target) {
        Method method = util.toActionInvocation(target).methodInvocation.getMethod();
        add(new NavigationItem(labelUtil.method(method).label(), iconUtil.tryGetIcon(method), Optional.of(target),
                new SameActionInvocationPredicate(util.toActionInvocation(target))));

        return this;
    }

    public NavigationBuilder text(String text) {
        return text(LString.of(text));
    }

    public NavigationBuilder hideWhenUnauthorized() {
        lastItem().hideWhenUnauthorized = true;
        return this;
    }

    public NavigationBuilder text(LString text) {
        lastItem().text = text;
        return this;
    }

    public NavigationBuilder icon(Renderable<Html5Canvas<?>> icon) {
        return icon(Optional.of(icon));
    }

    public NavigationBuilder icon(Optional<Renderable<Html5Canvas<?>>> icon) {
        lastItem().icon = icon;
        return this;
    }

    public NavigationBuilder add(ActionResult target, LString text) {
        return add(target).text(text);
    }

    public NavigationBuilder group(String text) {
        return group(LString.of(text));
    }

    public NavigationBuilder group(Enum<?> labelEnum) {
        return group(labelUtil.enumMember(labelEnum).label());
    }

    public NavigationBuilder group(LString text) {
        NavigationItem group = new NavigationItem(text, Optional.empty(), Optional.empty(), x -> false);
        add(group);
        currentGroup.push(group);
        return this;
    }

    public NavigationBuilder _group() {
        currentGroup.pop();
        return this;
    }

    public Navigation getResult() {
        return result;
    }
}
