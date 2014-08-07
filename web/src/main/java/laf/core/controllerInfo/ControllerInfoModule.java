package laf.core.controllerInfo;

import laf.base.BaseModule;

import org.jabsaw.Module;
/*
 * @startuml doc-files/overview.png
 * 
 * class ControllerInfo {
 *   getName(): String
 *   getType(): Object
 * }
 * ControllerInfo ->  MethodInfo 
 * 
 * class MethodInfo {
 *   getName(): String
 * }
 * MethodInfo ->  ParameterInfo
 * 
 * class ParameterInfo{
 *   getType(): Type
 * }
 * 
 * @enduml
 */
/**
 * Module containing information about all available controllers.
 * 
 * <p>
 * In the LAF, all requests sent by the client are handled by a controller. A controller
 * has action methods which handle the individual requests. A controller is represented
 * by a {@link ControllerInfo} instance.
 * </p>
 * 
 * <img src="doc-files/overview.png" />
 * <p>
 * The creation of {@link ControllerInfo} instances is fully customizable. See
 * {@link ControllerInfoRepository} for details.
 * </p>
 */
@Module(imported = { BaseModule.class })
public class ControllerInfoModule {

}
