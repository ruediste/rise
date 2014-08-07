package laf.core.actionPath;

import javax.enterprise.inject.Alternative;

import laf.base.ActionResult;

/**
 * An action method returns an {@link ActionResult}. When using the
 * {@link ActionPathFactory} to create {@link ActionPath}s, instances of this
 * class is used to represent the result.
 */
@Alternative
public class PathActionResult extends ActionPath<Object> implements
ActionResult {

}
