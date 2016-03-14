package com.github.ruediste.rise.testApp.persistence;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.persistence.NoTransaction;
import com.github.ruediste.rise.core.persistence.Updating;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste.rise.testApp.TestCanvas;
import com.github.ruediste.rise.testApp.ViewMvc;
import com.github.ruediste1.i18n.label.Labeled;

public class EntityControllerMvc extends ControllerMvc<EntityControllerMvc> {

    @Labeled
    static class ListView extends ViewMvc<EntityControllerMvc, List<TestAppEntity>> {

        @Override
        public void renderContent(TestCanvas html) {
            html.ul().render(x -> {
                for (TestAppEntity item : data())
                    x.li().content(Objects.toString(item.getId()));
            })._ul();
        }

    }

    @Inject
    TestEntityRepository repo;

    @Inject
    EntityManager em;

    @UrlUnsigned
    public ActionResult index() {
        return view(ListView.class, repo.getAll());
    }

    @NoTransaction
    @UrlUnsigned
    public ActionResult indexNoTransaction() {
        return view(ListView.class, repo.getAll());
    }

    @Updating
    @UrlUnsigned
    public ActionResult delete(TestAppEntity entity) {
        em.remove(entity);
        return redirect(go().index());
    }
}
