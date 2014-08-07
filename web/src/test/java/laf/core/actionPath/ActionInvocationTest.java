package laf.core.actionPath;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.jabsaw.util.Modules;
import org.junit.Test;

public class ActionInvocationTest {

	@Test
	public void test() {
		ArrayList<String> errors = new ArrayList<>();
		Modules.getProjectModel();
		Modules.checkClassAccessibility(errors);
		assertTrue(errors.toString(), errors.isEmpty());
	}
}
