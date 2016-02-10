package com.github.ruediste.rise.crud;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

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
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CComponentStack;
import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.CDataGrid;
import com.github.ruediste.rise.component.components.CFormGroup;
import com.github.ruediste.rise.component.components.CInput;
import com.github.ruediste.rise.component.components.CSelect;
import com.github.ruediste.rise.component.components.CSwitch;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.components.CValue;
import com.github.ruediste.rise.component.components.InputType;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentTreeUtil;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.crud.CrudUtil.CrudList;
import com.github.ruediste.rise.crud.CrudUtil.CrudPicker;
import com.github.ruediste.rise.crud.CrudUtil.CrudPickerFactory;
import com.github.ruediste.rise.crud.CrudUtil.IdentificationRenderer;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;
import com.google.common.primitives.Primitives;

/**
 * Please not that components can operate directly on the entity instead of
 * waiting for the binding to be triggered.
 */
@Singleton
public class CrudEditComponents extends
        FactoryCollection<CrudPropertyInfo, CrudEditComponents.CrudEditComponentFactory> {
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
        Component create(CrudPropertyHandle handle);
    }

    private Component toComponent(Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponent(renderer);
    }

    abstract class Targets {
        @GlyphiconIcon(Glyphicon.open)
        @Labeled
        abstract void pick();
    }

    public Component createEditComponent(CrudPropertyInfo property,
            CrudPropertyHandle handle) {

        return getFactory(property).create(handle);
    }

    @PostConstruct
    public void initialize() {
        addStringFactory();
        addNumberFactory(Long.class, Long::parseLong);
        addNumberFactory(Integer.class, Integer::parseInt);

        addManyToOneFactory();
        addOneToManyFactory();

        addEnumElementCollectionFactory();

    }

    public void addEnumElementCollectionFactory() {
        addFactory(p -> {
            if (p.getAttribute()
                    .getPersistentAttributeType() != PersistentAttributeType.ELEMENT_COLLECTION)
                return false;
            PluralAttribute<?, ?, ?> pluralAttribute = (PluralAttribute<?, ?, ?>) p
                    .getAttribute();
            return pluralAttribute.getElementType().getJavaType().isEnum();
        } , (decl) -> {
            PluralAttribute<?, ?, ?> pluralAttribute = (PluralAttribute<?, ?, ?>) decl
                    .info().getAttribute();
            Class<?> enumType = pluralAttribute.getElementType().getJavaType();
            return toComponent(new Renderable<BootstrapRiseCanvas<?>>() {

                private Collection<?> targetCollection() {
                    return (Collection<?>) decl.getValue();
                }

                @Override
                public void renderOn(BootstrapRiseCanvas<?> html) {
                    CDataGrid<Object> grid = new CDataGrid<Object>();
                    grid.addColumn(() -> new CDataGrid.Cell(r -> "Name"),
                            o -> new CDataGrid.Cell(r -> String.valueOf(o)))

                            .addColumn(() -> new CDataGrid.Cell(r -> ""),
                                    o -> new CDataGrid.Cell(new CButton(
                                            CrudEditComponents.this,
                                            x -> x.remove(() -> grid
                                                    .updateItems(i -> i
                                                            .remove(o))))))

                            .bind(() -> decl.proxy(), (g, obj) -> g.setItems(
                                    new ArrayList<Object>(targetCollection())),
                                    (g, obj) -> {
                        targetCollection().clear();
                        ((Collection) targetCollection()).addAll(g.getItems());

                    });
                    CSelect<Object> select = new CSelect<>()
                            .setItems(
                                    Arrays.asList(enumType.getEnumConstants()))
                            .setAllowEmpty(true);
                    html.bFormGroup().label()
                            .content(labelUtil.getPropertyLabel(
                                    decl.info().getProperty()))
                            .add(grid).add(select)
                            .add(new CButton(CrudEditComponents.this,
                                    x -> x.add(() -> select.getSelectedItem()
                                            .map(o -> grid.updateItems(
                                                    i1 -> i1.add(o))))))
                            ._bFormGroup();
                }
            });
        });
    }

    public void addStringFactory() {
        addFactory(
                decl -> String.class.equals(decl.getAttribute().getJavaType()),
                (decl) -> new CFormGroup(new CTextField()
                        .setLabel(labelUtil
                                .getPropertyLabel(decl.info().getProperty()))
                        .bindText(() -> (String) decl.getValue())));
    }

    public void addManyToOneFactory() {
        // Many to One
        addFactory(
                decl -> decl.getAttribute()
                        .getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE,
                (decl) -> {
                    Class<?> cls = decl.info().getAttribute().getJavaType();

                    CValue<Object> cValue = new CValue<>(
                            v -> toComponent(html -> crudUtil
                                    .getStrategy(IdentificationRenderer.class,
                                            cls)
                                    .renderIdenification(html, v)))
                                            .bindValue(() -> decl.getValue());

                    //@formatter:off
                    return toComponent(html -> html
                            .bFormGroup()
                                .label().content(labelUtil.getPropertyLabel(decl.info().getProperty()))
                                .bInputGroup()
                                .span().BformControl().DISABLED("disbled").TEST_NAME(decl.info().getAttribute().getName())
                                    .add(cValue)
                                ._span()
                                .bInputGroupBtn()
                                    .add(new CButton(this,(btn, c)->c.pick(()->{
                                        CrudPicker picker = crudUtil.getStrategy(CrudPickerFactory.class, cls)
                                                .createPicker(decl.info().getEmQualifier(), cls);
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addOneToManyFactory() {
        //@formatter:off
        addFactory(
                p -> p.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_MANY,
                (decl) -> toComponent(html->html
                        .bFormGroup()
                            .label().content(labelUtil.getPropertyLabel(decl.info().getProperty()))
                            .div()
                                .add(new CButton(this,
                                (btn, x) -> x.chooseItems(() -> {
                                    PluralAttribute attr=(PluralAttribute) decl.info().getAttribute();
                                    OneToMany oneToMany = ((AnnotatedElement)attr.getJavaMember()).getAnnotation(OneToMany.class);
                                    Collection collection = (Collection) decl.getValue();
                                    
                                    CrudList list =
                                        crudUtil
                                        .getStrategy(CrudUtil.CrudListFactory.class,attr.getElementType().getJavaType())
                                        .createList(
                                                decl.info().getEmQualifier(),
                                            attr.getElementType().getJavaType(),null
                                        );
                                    list.setItemActionsFactory(obj->{
                                        CSwitch<Boolean> result=new CSwitch<>();
                                        if ("".equals(oneToMany.mappedBy())){
                                            // this is the owning side
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
                                                owningProperty.setValue(obj, decl.rootEntity());
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

    private <T> void addNumberFactory(Class<T> boxCls,
            Function<String, T> parse) {
        addFactory(
                decl -> boxCls.equals(
                        Primitives.wrap(decl.getAttribute().getJavaType())),
                (decl) -> {
                    CInput input = new CInput(InputType.number)
                            .setLabel(labelUtil.getPropertyLabel(
                                    decl.info().getProperty()))
                            .TEST_NAME(decl.info().getAttribute().getName());

                    BindingUtil.bind(input, decl.group(),
                            entity -> input
                                    .setValue(String.valueOf(decl.getValue())),
                            entity -> decl
                                    .setValue(parse.apply(input.getValue())));
                    return new CFormGroup(input);
                });
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
