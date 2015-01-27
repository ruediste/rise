package laf.testApp.component;

import javax.inject.Inject;

import com.github.ruediste.laf.core.base.ActionResult;
import com.github.ruediste.laf.mvc.core.api.MController;
import com.github.ruediste.laf.mvc.web.MWControllerUtil;

import laf.testApp.dom.TestEntity;

@MController
public class EntityDisplayController {

	@Inject
	MWControllerUtil util;

	public ActionResult index(TestEntity entity) {
		return util.view(EntityDisplayView.class, entity != null ? entity
				: new TestEntity());
	}
}
