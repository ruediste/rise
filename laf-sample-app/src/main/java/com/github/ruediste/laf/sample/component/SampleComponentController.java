package com.github.ruediste.laf.sample.component;

import javax.inject.Inject;

import com.github.ruediste.laf.component.IComponentController;
import com.github.ruediste.laf.component.core.binding.BindingGroup;
import com.github.ruediste.laf.core.ActionResult;

public class SampleComponentController implements IComponentController {

	int counter;

	@Inject
	BindingGroup<Data> data;

	public static class Data {
		private String text;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}

	public Data getData() {
		return data.proxy();
	}

	void inc() {
		counter++;
		data.pushDown();
		System.out.println(data.get().getText());
	}

	public ActionResult index() {
		data.set(new Data());
		return null;
	}
}
