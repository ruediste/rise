package com.github.ruediste.rise.crud;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.FrameworkViewComponent;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CButtonTemplate;
import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.CDataGrid.Cell;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.crud.CrudUtil.CrudList;
import com.github.ruediste.rise.crud.CrudUtil.CrudPicker;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste.rise.util.GenericEvent;
import com.github.ruediste.rise.util.GenericEventManager;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.message.TMessage;
import com.github.ruediste1.i18n.message.TMessages;

public class DefaultCrudPickerController extends SubControllerComponent
        implements CrudPicker {

    @Inject
    CrudUtil util;

    private CrudList ctrl;

    public static class View
            extends FrameworkViewComponent<DefaultCrudPickerController> {

        @TMessages
        public interface Messages {

            @TMessage("Picker for {clazz}")
            LString pickerFor(LString clazz);
        }

        @Inject
        Messages messages;

        @Override
        protected Component createComponents() {
            return toComponent(
                    html -> html.h1()
                            .content(messages.pickerFor(label(controller.ctrl
                                    .getType().getEntityClass())))
                    .add(new CController(controller.ctrl)));
        }

    }

    private GenericEventManager<Object> pickerClosed = new GenericEventManager<>();

    @Labeled
    @GlyphiconIcon(Glyphicon.check)
    public void pick(Object item) {
        pickerClosed.fire(item);
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.arrow_left)
    public void cancel() {
        pickerClosed.fire(null);
    }

    @Override
    public GenericEvent<Object> pickerClosed() {
        return pickerClosed.event();
    }

    public CrudPicker initialize(Class<?> entityClass,
            Class<? extends Annotation> emQualifier) {
        ctrl = util.getStrategy(CrudUtil.CrudListFactory.class, entityClass)
                .createList(emQualifier, entityClass, null);
        ctrl.setBottomActions(new CButton(this, c -> c.cancel()));
        ctrl.setItemActionsFactory(
                item -> new Cell(new CButton(this, c -> c.pick(item), true)
                        .apply(CButtonTemplate.setArgs(x -> x.primary()))));
        return this;
    }

}