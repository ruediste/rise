package com.github.ruediste.rise.sample.db;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.db.TodoController.IndexData;
import com.github.ruediste1.i18n.label.Label;

@Label("Todo Items")
public class TodoView extends PageView<TodoController, IndexData> {

    @Inject
    Logger log;

    @Override
    public void renderBody(SampleCanvas html) {

        // @formatter:off
		html.bRow()
		    .bCol(x->x.xs(12))
		        .h1().content("Todo Items")
		    ._bCol()
		._bRow()
		.fForEach(data().allItems, item->{
		    html.bRow()
		        .bCol(x->x.xs(9))
		            .write(item.getName())
		        ._bCol()
		        .bCol(x->x.xs(3))
		            .bButtonA().HREF(go().delete(item)).content("delete")
		        ._bCol()
		    ._bRow();
		})
		.form().ACTION(go().add()).METHOD("POST")
		    .bRow()
		        .bCol(x->x.xs(9))
				    .input().BformControl().TYPE("text").NAME("name")
				._bCol()
				.bCol(x->x.xs(3))
					.input().BformControl().TYPE("submit").VALUE("add")
				._bCol()
			._bRow()
		._form();
	}

}
