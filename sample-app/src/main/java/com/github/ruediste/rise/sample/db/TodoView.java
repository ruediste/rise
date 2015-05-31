package com.github.ruediste.rise.sample.db;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.db.TodoController.IndexData;

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
		.form().ACTION(url(go().add())).METHOD("POST")
		    .bRow()
		        .bCol(x->x.xs(9))
				    .input().B_FORM_CONTROL().TYPE("text").NAME("name")
				._bCol()
				.bCol(x->x.xs(3))
					.input().B_FORM_CONTROL().TYPE("submit").VALUE("add")
				._bCol()
			._bRow()
		._form();
	}

    @Override
    public String getTitle() {
        return "Todo Items";
    }
}
