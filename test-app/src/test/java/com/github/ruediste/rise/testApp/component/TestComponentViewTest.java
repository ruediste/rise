package com.github.ruediste.rise.testApp.component;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;

import com.github.ruediste.rise.component.ComponentViewRepository;
import com.github.ruediste.rise.testApp.WebTest;

public class TestComponentViewTest extends WebTest {

    @Inject
    ComponentViewRepository repo;

    @Test
    public void viewForSampleControllerFound() {
        assertEquals(TestComponentView.class, repo.createView(new TestComponentController()).getClass());
        assertEquals(TestComponentViewAlternative.class,
                repo.createView(new TestComponentController(), TestViewQualifier.class).getClass());
    }
}
