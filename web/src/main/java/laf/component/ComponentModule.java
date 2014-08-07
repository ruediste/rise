package laf.component;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import laf.base.BaseModule;
import laf.component.core.ComponentCoreModule;
import laf.component.core.ControllerUtil;
import laf.core.controllerInfo.ControllerInfoModule;

import org.jabsaw.Module;

/*
 * @startuml doc-files/testImage.png
 * Bob -> Alice : hello
 * Alice --> Bob : OK
 * @enduml
 */

/**
 *
 * <strong> Persistence </strong>
 * <p>
 * Component controllers use extended persistence contexts (PCs). During the
 * initial action method invocation, a transaction which will be commited is
 * active, keeping the PC open. During page reloads, no transaction is active.
 * Any lazy loading will occur in it's own short transaction, changes will not
 * be flushed to the database.
 * </p>
 *
 * <p>
 * If the page is left, the PC is closed and all open changes are lost. To
 * commit, one of the commit methods of {@link ControllerUtil} are used. For a
 * commit, a serializable transaction is started. If an optional checking
 * runnable is provided, that runnable is executed in the transaction using a
 * fresh PC. This allows to perform checks without the risk of using stale data
 * retrieved in previous transactions. The PC is flushed and closed after
 * executing the checker runnable. Onlu closing the PC without flushing cannot
 * be done, since the changes which would get flushed are implementation
 * dependent.
 * </p>
 *
 * <p>
 * Then the page PC is joined to the transaction. If an optional commit runnable
 * is provided, it is executed in the transaction with the page PC. Finally, the
 * PC is flushed and the transaction commited.
 * </p>
 *
 * <strong> Error Handling </strong>
 * <p>
 * At any time performing page initialization, reload or commits, exceptions
 * might occur. They should not be caught by the application code, except to
 * perform custom cleanup. Since exceptions might originate from JPA, we can not
 * rely on the ability to access any entities after it has been thrown. Since
 * such accesses might go unnoticed and might easily slip through testing, the
 * framework keeps track of a so-called error destination at any time. If an
 * exception reaches the framework, the user will be redirected to the error
 * destination.
 * </p>
 *
 * <p>
 * When an action method is first invoked, the current error destination is
 * undefined. If an error occurs in this situation, a configurable application
 * wide strategy is used to determine the destination. At any time, the
 * controller can set an error destination using
 * {@link ControllerUtil#setErrorDestination(laf.base.ActionResult)}.
 * </p>
 *
 * <strong> Managing Persistence Contexts</strong>
 * <p>
 * Unfortunately, we can not rely on the standard Java EE mechanisms to manage
 * the {@link EntityManager}s due to the rules of PC propagation: We need an
 * extended PC outside of a JTA transaction to propagate with the control flow,
 * which is not possible using the standard Java EE mechanism. Therefore we
 * require {@link EntityManager}s to be injected via the {@link Inject}
 * annotation (NOT the {@link PersistenceContext} annotation) and manage them as
 * required by the framework.
 * </p>
 *
 * <p>
 * <img src="doc-files/requestHandlingPipelineComponent.png"/>
 * </p>
 */
@Module(description = "Top level module of the component framework", imported = {
		ControllerInfoModule.class, ComponentCoreModule.class, BaseModule.class })
public class ComponentModule {
}
