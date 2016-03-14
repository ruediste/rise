package com.github.ruediste.rise.component.validation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.component.components.CFormGroup;
import com.github.ruediste.rise.component.components.CGroup;
import com.github.ruediste.rise.core.i18n.ValidationFailure;
import com.github.ruediste.rise.core.i18n.ValidationFailureImpl;
import com.github.ruediste.rise.core.i18n.ValidationFailureSeverity;

public class ValidationStatusRepositoryTest {

    ValidationStatusRepository repo;

    CGroup parent;
    CGroup child;
    CFormGroup presenterSubChild;

    Object source1 = new Object();
    Object source2 = new Object();

    ValidationFailure failure1 = new ValidationFailureImpl(l -> "foo1", ValidationFailureSeverity.INFO);
    ValidationFailure failure2 = new ValidationFailureImpl(l -> "foo2", ValidationFailureSeverity.INFO);

    @Before
    public void before() {
        repo = new ValidationStatusRepository();
        parent = new CGroup();
        child = new CGroup();
        presenterSubChild = new CFormGroup();
        parent.add(child);
        child.add(presenterSubChild);
    }

    @Test
    public void failureFound() {
        repo.addFailures(source1, parent, Arrays.asList(failure1));
        List<ValidationFailure> failures = repo.getValidationFailures(parent);
        assertEquals(1, failures.size());
        assertEquals(failure1, failures.get(0));
    }

    @Test
    public void childFailureFound() {
        repo.addFailures(source1, child, Arrays.asList(failure1));
        List<ValidationFailure> failures = repo.getValidationFailures(parent);
        assertEquals(1, failures.size());
        assertEquals(failure1, failures.get(0));
        assertEquals(0, repo.getDirectValidationFailures(parent).size());
        assertEquals(1, repo.getDirectValidationFailures(child).size());
    }

    @Test
    public void subChildFailureNot() {
        repo.addFailures(source1, presenterSubChild, Arrays.asList(failure1));
        assertEquals(0, repo.getDirectValidationFailures(parent).size());
        assertEquals(0, repo.getDirectValidationFailures(child).size());
        assertEquals(1, repo.getValidationFailures(presenterSubChild).size());
    }

    @Test
    public void sourceFailuresRemoved() {
        repo.addFailures(source1, parent, Arrays.asList(failure1));
        assertEquals(1, repo.getValidationFailures(parent).size());
        repo.addFailures(source2, parent, Arrays.asList(failure2));
        assertEquals(2, repo.getValidationFailures(parent).size());
        repo.clearFailures(source1);
        assertEquals(1, repo.getValidationFailures(parent).size());
        assertEquals(failure2, repo.getValidationFailures(parent).get(0));
    }
}
