package laf.core.requestProcessing;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import laf.core.actionPath.ActionPath;

@RequestScoped
public class ObjectActionPathProducer {

	private ActionPath<Object> path;

	public ActionPath<Object> getPath() {
		return path;
	}

	public void setPath(ActionPath<Object> path) {
		this.path = path;
	}

	@Produces
	@RequestScoped
	public ActionPath<Object> producePath() {
		return path;
	}
}
