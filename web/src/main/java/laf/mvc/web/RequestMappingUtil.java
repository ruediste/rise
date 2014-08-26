package laf.mvc.web;

import laf.mvc.actionPath.ActionPath;

public interface RequestMappingUtil {

	String generate(ActionPath<Object> path);

}
