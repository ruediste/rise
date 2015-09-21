package com.github.ruediste.rise.crud;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.OneToMany;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CComponentStack;
import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.CDataGrid;
import com.github.ruediste.rise.component.components.CFormGroup;
import com.github.ruediste.rise.component.components.CInput;
import com.github.ruediste.rise.component.components.CSwitch;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.components.CValue;
import com.github.ruediste.rise.component.components.InputType;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentTreeUtil;
import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.crud.CrudUtil.CrudList;
import com.github.ruediste.rise.crud.CrudUtil.CrudPicker;
import com.github.ruediste.rise.crud.CrudUtil.CrudPickerFactory;
import com.github.ruediste.rise.crud.CrudUtil.IdentificationRenderer;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;

/**
 * Please not that components can operate directly on the entity instead of
 * waiting for the binding to be triggered.
 */
@Singleton
public class CrudEditComponents extends
        FactoryCollectionNew<PersistentProperty, CrudEditComponents.CrudEditComponentFactory> {
    @Inject
    ComponentFactoryUtil util;

    @Inject
    CrudUtil crudUtil;
    @Inject
    LabelUtil labelUtil;

    @Inject
    RisePersistenceUtil persistenceUtil;

    @Inject
    ComponentUtil componentUtil;

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

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void initialize() {
        addFactory(
                decl -> String.class.equals(decl.getAttribute().getJavaType()),
                (decl, entityType,
                        group) -> new CFormGroup(new CTextField()
                                .setLabel(labelUtil
                                        .getPropertyLabel(decl.getProperty()))
                                .bindText(() -> (String) decl.getProperty()
                                        .getValue(group.proxy()))));

        addFactory(
                decl -> Long.TYPE.equals(decl.getAttribute().getJavaType())
                        || Long.class.equals(decl.getAttribute().getJavaType()),
                (decl, entityType, group) -> {
                    CInput input = new CInput(InputType.number)
                            .setLabel(labelUtil
                                    .getPropertyLabel(decl.getProperty()))
                            .TEST_NAME(decl.getAttribute().getName());

                    BindingUtil
                            .bind(input, group,
                                    entity -> input.setValue(String.valueOf(decl
                                            .getProperty().getValue(entity))),
                            entity -> decl.getProperty().setValue(entity,
                                    Long.parseLong(input.getValue())));
                    return new CFormGroup(input);
                });

        addFactory(
                decl -> decl.getAttribute()
                        .getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE,
                (decl, entityType, group) -> {
                    Class<?> cls = decl.getAttribute().getJavaType();

                    CValue<Object> cValue = new CValue<>(
                            v -> toComponent(html -> crudUtil
                                    .getStrategy(IdentificationRenderer.class,
                                            cls)
                                    .renderIdenification(html, v)))
                                            .bindValue(() -> decl.getProperty()
                                                    .getValue(group.proxy()));

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

        //@formatter:off
        addFactory(
                p -> p.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_MANY,
                (decl, entityType, group) -> toComponent(html->html
                        .bFormGroup()
                            .label().content(labelUtil.getPropertyLabel(decl.getProperty()))
                            .div()
                                .add(new CButton(this,
                                (btn, x) -> x.chooseItems(() -> {
                                    Object entity = group.get();
                                    PluralAttribute attr=(PluralAttribute) decl.getAttribute();
                                    OneToMany oneToMany = ((AnnotatedElement)attr.getJavaMember()).getAnnotation(OneToMany.class);
                                    Collection collection = (Collection) decl.getValue(entity);
                                    
                                    CrudList list =
                                        crudUtil
                                        .getStrategy(CrudUtil.CrudListFactory.class,entity.getClass())
                                        .createList(
                                                entityType.getEmQualifier(),
                                            attr.getElementType().getJavaType(),null
                                        );
                                    list.setItemActionsFactory(obj->{
                                        CSwitch<Boolean> result=new CSwitch<>();
                                        if ("".equals(oneToMany.mappedBy())){
                                            result.put(true, new CButton(this,c->c.remove(()->{
                                                collection.remove(obj);
                                                result.setOption(false);
                                            })));
                                            result.put(false, new CButton(this,c->c.add(()->{
                                                collection.add(obj);
                                                result.setOption(true);
                                            })));
                                        }
                                        else{
                                            PropertyInfo owningProperty = PropertyUtil.getPropertyInfo(attr.getElementType().getJavaType(), oneToMany.mappedBy());
                                            result.put(true, new CButton(this,c->c.remove(()->{
                                                collection.remove(obj);
                                                owningProperty.setValue(obj, null);
                                                result.setOption(false);
                                            })));
                                            result.put(false, new CButton(this,c->c.add(()->{
                                                collection.add(obj);
                                                owningProperty.setValue(obj, entity);
                                                result.setOption(true);
                                            })));
                                        }
                                        result.setOption(collection.contains(obj));
                                        return new CDataGrid.Cell(result);
                                    });
                                    list.setBottomActions(new CButton(
                                            this,
                                            x1 -> x1.back(() -> ComponentTreeUtil
                                                    .raiseEvent(
                                                            btn,
                                                            new CComponentStack.PopComponentEvent()))));

                                    ComponentTreeUtil.raiseEvent(btn,
                                            new CComponentStack.PushComponentEvent(
                                                    new CController(list)));
                                })))
                            ._div()
                        ._bFormGroup()));
        //@formatter:on

    }

    @Labeled
    @GlyphiconIcon(Glyphicon.edit)
    void chooseItems(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.plus)
    void add(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.minus)
    void remove(Runnable callback) {
        callback.run();
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

    @Labeled
    @GlyphiconIcon(Glyphicon.arrow_left)
    void back(Runnable callback) {
        callback.run();
    }
}
