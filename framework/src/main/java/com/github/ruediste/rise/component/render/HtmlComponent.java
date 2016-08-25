package com.github.ruediste.rise.component.render;

import java.util.Optional;

/**
 * A component rendering some HTML
 * 
 * <p>
 * <b>Rendering Overview</b><br>
 * The basic idea is to freshly render the whole view upon each page reload,
 * based on the view state. Together with the HTML a component tree is
 * constructed. This tree is used during the reload to handle value updates and
 * events and to keep component state over reloads.
 * </p>
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li><b>Component State:</b> Each component can have state which is kept over
 * page reloads. When a component is added to the tree during a page reload, all
 * fields marked with {@link ComponentState} are copied over from the
 * corresponding component of the previous render process. For details of the
 * matching process see below. If a field contains an {@link Optional}, it is
 * only copied if the target optional is {@link Optional#empty()}. The matching
 * happens immediatley after adding the component, before rendering. Otherwise
 * the state would not be available for rendering. *
 * <p>
 * Matching always happens among the children of the current parent component.
 * If a key is specified for a component, the old component with the same key is
 * used for matching. Otherwise the key is formed from the class of the
 * component and it's index. There is a separate index sequence for each
 * component class. Thus if the component sequence is A A B A C, the keys are
 * (A:1) (A:2) (B:1) (A:3) (C:1)
 * </p>
 * </li>
 * 
 * <li><b>Inspection of the Component Tree:</b> In many cases it is helpful to
 * be able to inspect the component tree to gain additional information or to
 * pass events. However, this is of little value if the rendering already
 * happened when such inspection becomes possible. Therefore a component can add
 * a placeholder during rendering which is evaluated during a second render
 * phase. There are two types of placeholders: attribute placeholders can only
 * add attributes to a single html tag. Tag placeholders can generate a whole
 * html tree, but have to start and end with a tag. All started tags have to be
 * closed. This property allows to check for well-fromedness of the generated
 * html during the initial rendering, as well as during the rendering of each
 * tag placeholder.</li>
 * 
 * <li><b>Validation:</b> Validation is always triggered by the controller. Both
 * the model and the components can be validated. The model will check domain
 * properties, the components the user input.
 * <p>
 * When a controller performs model validation, the validation failures are
 * stored in a field of the controller. The components inspect their bindings
 * and extract the applicable validation failures. The validation presenters
 * inspect a part of the component trees and display any validation failures
 * found. A validation presenter can also inspect the failures present on a
 * controller and display those not othewise displayed.
 * </p>
 * <p>
 * In addition, the controller can set a flag which causes the components to
 * perform their own validations which are displayed by the validation
 * presenters, too.
 * </p>
 * <li><b>Partial page reloads:</b></li> For fast page changes, pages can be
 * reloaded partially. This is achieved by implementing a part of the page
 * rendering as lambda function, which will be re-evaluated for the partial
 * rendering. All handlers (event, value, ajax) are associated with the
 * compoents, thus throwing away and recreating part of the page is sufficient.
 * <li><b>Ajax:</b> The client can send ajax requests, which can be handled by
 * individual components</li>
 * </ul>
 * </p>
 */
public class HtmlComponent {

}
