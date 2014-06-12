package laf.component.pageScope;

import java.lang.annotation.Annotation;

import javax.enterprise.context.spi.*;

public class PageContext implements Context {

	@Override
	public Class<? extends Annotation> getScope() {
		return PageScoped.class;
	}

	@Override
	public <T> T get(Contextual<T> component,
			CreationalContext<T> creationalContext) {
		PageScopeHolder holder = PageScopeManager.getInstance()
				.getCurrentHolder();
		return holder.getOrCreate(component, creationalContext);
	}

	@Override
	public <T> T get(Contextual<T> component) {
		PageScopeHolder holder = PageScopeManager.getInstance()
				.getCurrentHolder();
		return holder.get(component);
	}

	@Override
	public boolean isActive() {
		return PageScopeManager.getInstance().isActive();
	}

}
