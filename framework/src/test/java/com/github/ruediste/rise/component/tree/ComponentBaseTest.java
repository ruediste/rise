package com.github.ruediste.rise.component.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;

import com.github.ruediste.rise.component.components.CGroup;

public class ComponentBaseTest {

    @Test
    public void isDisabledNoParent() {
        CGroup component = new CGroup();
        assertFalse(component.isDisabled());
        component.disable();
        assertTrue(component.isDisabled());
    }

    @Test
    public void isDisabledWithInheritance() {
        CGroup a = new CGroup();
        CGroup b = new CGroup();
        CGroup c = new CGroup();
        a.add(b.add(c));

        Set<Optional<Boolean>> states = new HashSet<>();
        states.add(Optional.empty());
        states.add(Optional.of(true));
        states.add(Optional.of(false));

        for (Optional<Boolean> stateA : states) {
            a.setDisabled(stateA);
            for (Optional<Boolean> stateB : states) {
                b.setDisabled(stateB);
                for (Optional<Boolean> stateC : states) {
                    c.setDisabled(stateC);
                    assertEquals(stateC.orElseGet(() -> stateB.orElseGet(() -> stateA.orElse(false))), c.isDisabled());
                    assertEquals(stateB.orElseGet(() -> stateA.orElse(false)), b.isDisabled());
                    assertEquals(stateA.orElse(false), a.isDisabled());
                }
            }
        }
    }
}
