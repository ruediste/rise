package laf.urlMapping;

import static org.mockito.Mockito.*;

import java.util.Arrays;

import laf.controllerInfo.ControllerInfoRepository;
import laf.controllerInfo.ControllerInfoRepositoryInitializer;
import laf.controllerInfo.impl.ControllerInfoImpl;
import laf.controllerInfo.impl.TestController;

import org.junit.Before;
import org.junit.Test;

public class DefaultUrlMappingRuleTest {

	DefaultUrlMappingRule rule;

	@Before
	public void init() {
		rule = new DefaultUrlMappingRule();
		rule.controllerInfoRepository = mock(ControllerInfoRepository.class);
		ControllerInfoRepositoryInitializer initializer = new ControllerInfoRepositoryInitializer();
		ControllerInfoImpl info = initializer
				.createControllerInfo(TestController.class);
		when(rule.controllerInfoRepository.getControllerInfos()).thenReturn(
				Arrays.asList(info));
		rule.initialize();
	}

	@Test
	public void generate() {
		ActionPath<Object> path = new ActionPath<>();
		ActionInvocation<Object> invocation = new ActionInvocation<>();

		rule.generate(path);
	}
}
