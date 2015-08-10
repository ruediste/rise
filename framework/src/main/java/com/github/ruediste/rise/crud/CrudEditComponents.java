package com.github.ruediste.rise.crud;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CComponentStack;
import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.CFormGroup;
import com.github.ruediste.rise.component.components.CInput;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.components.CValue;
import com.github.ruediste.rise.component.components.InputType;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentTreeUtil;
import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.crud.CrudUtil.CrudPicker;
import com.github.ruediste.rise.crud.CrudUtil.CrudPickerFactory;
import com.github.ruediste.rise.crud.CrudUtil.IdentificationRenderer;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;

@Singleton
public class CrudEditComponents
        extends
        FactoryCollectionNew<PersistentProperty, CrudEditComponents.CrudEditComponentFactory> {
    @Inject
    ComponentFactoryUtil util;

    @Inject
    CrudUtil crudUtil;
    @Inject
    LabelUtil labelUtil;

    @Inject
    RisePersistenceUtil persistenceUtil;

    public interface CrudEditComponentFactory {
        Component create(PersistentProperty decl, PersistentType entityType,
                BindingGroup<?> group);
    }

    private Component toComponent(Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponent(renderer);
    }

    abstract class Targets {
        @GlyphiconIcon(Glyphicon.open)
        @Labeled
        abstract void pick();
    }

    public Component createEditComponent(PersistentProperty property,
            PersistentType entityType, BindingGroup<?> group) {
        return getFactory(property).create(property, entityType, group);
    }

    @PostConstruct
    public void initialize() {
        addFactory(
                decl -> String.class.equals(decl.getAttribute().getJavaType()),
                (decl, entityType, group) -> new CFormGroup(new CTextField()
                        .setLabel(
                                labelUtil.getPropertyLabel(decl.getProperty()))
                        .bindText(
                                () -> (String) decl.getProperty().getValue(
                                        group.proxy()))));

        addFactory(
                decl -> Long.TYPE.equals(decl.getAttribute().getJavaType())
                        || Long.class.equals(decl.getAttribute().getJavaType()),
                (decl, entityType, group) -> {
                    CInput input = new CInput(InputType.number).setLabel(
                            labelUtil.getPropertyLabel(decl.getProperty()))
                            .TEST_NAME(decl.getAttribute().getName());

                    BindingUtil.bind(
                            input,
                            group,
                            entity -> input.setValue(String.valueOf(decl
                                    .getProperty().getValue(entity))),
                            entity -> decl.getProperty().setValue(entity,
                                    Long.parseLong(input.getValue())));
                    return new CFormGroup(input);
                });

        addFactory(
                decl -> decl.getAttribute().getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE,
                (decl, entityType, group) -> {
                    Class<?> cls = decl.getAttribute().getJavaType();

                    CValue<Object> cValue = new CValue<>(
                            v -> toComponent(html -> crudUtil.getStrategy(
                                    IdentificationRenderer.class, cls)
                                    .renderIdenification(html, v)))
                            .bindValue(() -> decl.getProperty().getValue(
                                    group.proxy()));

                    //@formatter:off
                    return toComponent(html -> html
                            .bFormGroup()
                                .label().content(labelUtil.getPropertyLabel(decl.getProperty()))
                                .bInputGroup()
                                .span().BformControl().DISABLED("disbled").TEST_NAME(decl.getAttribute().getName())
                                    .add(cValue)
                                ._span()
                                .bInputGroupBtn()
                                    .add(new CButton(this,(btn, c)->c.pick(()->{
                                        CrudPicker picker = crudUtil.getStrategy(CrudPickerFactory.class, cls)
                                                .createPicker(entityType.getEmQualifier(), cls);
                                        picker.pickerClosed().addListener(value->{
                                            if (value!=null){
                                                cValue.setValue(value);
                                            }
                                            ComponentTreeUtil
                                            .raiseEvent(btn, new CComponentStack.PopComponentEvent());
                                        });
                                        ComponentTreeUtil
                                                .raiseEvent(btn, new CComponentStack.PushComponentEvent(
                                                        new CController(picker)));
                                    })))
                                    .add(new CButton(this,c->c.clear(()->cValue.setValue(null))))
                                ._bInputGroupBtn()
                                ._bInputGroup()
                            ._bFormGroup());
                    //@formatter:on);
                });
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.hand_right)
    void pick(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.remove)
    void clear(Runnable callback) {
        callback.run();
    }

}
