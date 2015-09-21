package com.github.ruediste.rise.core.navigation;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Predicate;

import javax.inject.Inject;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.navigation.Navigation.NavigationItem;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;

public class NavigationBuilder {

    private final class SameActionInvocationPredicate
            implements Predicate<ActionInvocation<Object>> {
        private ActionInvocation<?> referenceInvocation;

        public SameActionInvocationPredicate(
                ActionInvocation<?> referenceInvocation) {
            this.referenceInvocation = referenceInvocation;
        }

        @Override
        public boolean test(ActionInvocation<Object> x) {
            return x.methodInvocation.getMethod()
                    .equals(referenceInvocation.methodInvocation.getMethod());
        }
    }

    @Inject
    CoreUtil util;

    @Inject
    LabelUtil labelUtil;

    private final Navigation result = new Navigation();

    private Deque<NavigationItem> currentGroup = new ArrayDeque<>();

    private NavigationBuilder add(NavigationItem item) {
        if (currentGroup.isEmpty())
            result.getRootItems().add(item);
        else
            currentGroup.peek().children.add(item);
        return this;
    }

    public NavigationBuilder add(ActionResult target) {
        Method method = util.toActionInvocation(target).methodInvocation
                .getMethod();
        return add(target, labelUtil.getMethodLabel(method));
    }

    public NavigationBuilder add(ActionResult target, String text) {
        return add(target, locale -> text);
    }

    public NavigationBuilder add(ActionResult target, LString text) {
        return add(new NavigationItem(text, Optional.of(target),
                new SameActionInvocationPredicate(
                        util.toActionInvocation(target))));
    }

    public NavigationBuilder group(String text) {
        return group(locale -> text);
    }

    public NavigationBuilder group(Enum<?> labelEnum) {
        return group(labelUtil.getEnumMemberLabel(labelEnum));
    }

    public NavigationBuilder group(LString text) {
        NavigationItem group = new NavigationItem(text, Optional.empty(),
                x -> false);
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
