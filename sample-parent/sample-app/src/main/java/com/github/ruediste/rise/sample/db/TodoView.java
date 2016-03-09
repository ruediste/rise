package com.github.ruediste.rise.sample.db;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.sample.PageView;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.db.TodoController.IndexData;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.MembersLabeled;

@Label("Todo Items")
public class TodoView extends PageView<TodoController, IndexData> {

    @Inject
    Logger log;

    @MembersLabeled
    private enum Labels {
        @Label("Todo Items") TITLE, ADD
    }

    @Override
    public void renderContent(SampleCanvas html) {

        // @formatter:off
		html.bRow()
		    .bCol(x->x.xs(12))
		        .h1().content(label(Labels.TITLE))
		    ._bCol()
		._bRow()
		.div().TEST_NAME("itemList")
		.fForEach(data().allItems, item->{
		    html.bRow()
		        .bCol(x->x.xs(9)).TEST_NAME("name")
		            .write(item.getName())
		        ._bCol()
		        .bCol(x->x.xs(3))
		            .rButtonA(go().delete(item))
		        ._bCol()
		    ._bRow();
		})
		._div()
		.form().ACTION(go().add()).METHOD("POST")
		    .bRow()
		        .bCol(x->x.xs(9))
				    .input().BformControl().TYPE("text").NAME("name").ID("addName")
				._bCol()
				.bCol(x->x.xs(3))
					.input().BformControl().TYPE("submit").TEST_NAME(go().add()).VALUE(resolve(label(Labels.ADD)))
				._bCol()
			._bRow()
		._form();
	}

}
