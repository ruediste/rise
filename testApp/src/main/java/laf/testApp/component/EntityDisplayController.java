package laf.testApp.component;

import javax.inject.Inject;

import laf.core.base.ActionResult;
import laf.mvc.core.api.MController;
import laf.mvc.web.MWControllerUtil;
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
