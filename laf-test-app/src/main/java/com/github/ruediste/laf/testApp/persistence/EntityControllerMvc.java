package com.github.ruediste.laf.testApp.persistence;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.api.ControllerMvcWeb;
import com.github.ruediste.laf.api.ViewMvcWeb;
import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.mvc.Updating;

public class EntityControllerMvc extends ControllerMvcWeb<EntityControllerMvc> {

	static class ListView extends
			ViewMvcWeb<EntityControllerMvc, List<TestEntity>> {

		@Override
		public void render(HtmlCanvas html) throws IOException {
			html.html().body().ul().render(x -> {
				for (TestEntity item : data())
					x.li().content(Objects.toString(item.getId()));
			})._ul()._body()._html();
		}

	}

	@Inject
	TestEntityRepository repo;

	@Inject
	EntityManager em;

	public ActionResult index() {
		return view(ListView.class, repo.getAll());
	}

	@Updating
	public ActionResult delete(TestEntity entity) {
		em.remove(entity);
		commit();
		return redirect(go().index());
	}
}
