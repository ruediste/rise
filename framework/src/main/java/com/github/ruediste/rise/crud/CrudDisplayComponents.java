package com.github.ruediste.rise.crud;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CComponentStack;
import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.CDataGrid;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentTreeUtil;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.crud.CrudUtil.CrudList;
import com.github.ruediste.rise.crud.CrudUtil.IdentificationRenderer;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.TypeToken;

@Singleton
public class CrudDisplayComponents extends
        FactoryCollection<CrudPropertyInfo, CrudDisplayComponents.CrudDisplayComponentFactory> {

    @Inject
    ComponentFactoryUtil util;

    @Inject
    LabelUtil labelUtil;

    @Inject
    CrudUtil crudUtil;

    @Inject
    RisePersistenceUtil persistenceUtil;

    @Inject
    ComponentUtil componentUtil;

    @Inject
    CrudReflectionUtil crudReflectionUtil;

    private Component toComponentBound(Supplier<?> bindingAccessor,
            Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponentBound(bindingAccessor, renderer);
    }

    private Component toComponent(Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponent(renderer);
    }

    public interface CrudDisplayComponentFactory {
        Component create(CrudPropertyHandle property);
    }

    public Component create(CrudPropertyHandle propertyHandle) {
        return getFactory(propertyHandle.info()).create(propertyHandle);
    }

    @SuppressWarnings({})
    public CrudDisplayComponents() {
        addNumbersFactory();
        addEnumElementCollectionFactory();
        addManyToOneFactory();
        addOneToManyFactory();
        addByteArrayFactory();
        addEmbeddedFactory();
    }

    public void addEmbeddedFactory() {
        addFactory(
                p -> p.getAttribute()
                        .getPersistentAttributeType() == PersistentAttributeType.EMBEDDED,
                handle -> toComponentBound(() -> handle.proxy(), html -> {

                    Object value = handle.getValue();
                    if (value == null) {
                        Class<?> rawType = TypeToken.of(
                                handle.info().getProperty().getPropertyType())
                                .getRawType();
                        try {
                            value = rawType.newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException(
                                    "Error while instantiating " + rawType
                                            + " for embeddable "
                                            + handle.info().getProperty()
                                            + " which was null");
                        }
                    }
                    Object finalValue = value;
                    html.bCol(x -> x.xs(11).xsOffset(1));
                    for (CrudPropertyInfo property : crudReflectionUtil
                            .getDisplayProperties(
                                    crudReflectionUtil.getPersistentType(
                                            handle.info().getEmQualifier(),
                                            value.getClass()))) {
                        CrudPropertyHandle subHandle = CrudPropertyHandle
                                .create(property, handle::rootEntity,
                                        () -> finalValue, handle.group());
                        html.bFormGroup().label()
                                .content(labelUtil.getPropertyLabel(
                                        subHandle.info().getProperty()))
                                .add(create(subHandle))._bFormGroup();
                    }
                    html._bCol();
                }));
    }

    public void addByteArrayFactory() {
        addFactory(p -> byte[].class.equals(p.getAttribute().getJavaType()),
                property -> toComponentBound(() -> property.proxy(), html -> {
                    byte[] value = (byte[]) property.getValue();
                    html.span().BformControl().DISABLED("disabled")
                            .TEST_NAME(property.info().getName())
                            .content(value == null ? "<null>"
                                    : BaseEncoding.base16().encode(value));
                }));
    }

    public void addEnumElementCollectionFactory() {
        addFactory(
                p -> p.getAttribute()
                        .getPersistentAttributeType() == PersistentAttributeType.ELEMENT_COLLECTION,
                (p) -> toComponentBound(() -> p.proxy(), html -> {
                    html.div()
                            .content(StreamSupport
                                    .stream(((Iterable<?>) p.getValue())
                                            .spliterator(), false)
                            .map(String::valueOf)
                            .collect(Collectors.joining(", ")));
                }));
    }

    @SuppressWarnings("rawtypes")
    public void addOneToManyFactory() {
        //@formatter:off
        addFactory(
                p -> p.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_MANY,
                (p) -> toComponent(html->html
                            .div()
                                .add(new CButton(this,
                                (btn, x) -> x.showItems(() -> {
                                    PluralAttribute attr=(PluralAttribute) p.info().getAttribute();
                                    Class<?> elementType = attr.getElementType().getJavaType();
                                    CrudList list =
                                        crudUtil
                                        .getStrategy(CrudUtil.CrudListFactory.class,elementType)
                                        .createList(
                                            p.info().getEmQualifier(),
                                            elementType,
                                            ctx -> {
                                                ctx.addWhere(ctx.root().in((Collection)p.getValue()));
                                            }
                                        );
                                    list.setItemActionsFactory(obj->new CDataGrid.Cell(
                                            new CButton(componentUtil.go(CrudControllerBase.class).display(obj))
                                            ));
                                    list.setBottomActions(new CButton(
                                            this,
                                            x1 -> x1.back(() -> ComponentTreeUtil
                                                    .raiseEvent(
                                                            btn,
                                                            new CComponentStack.PopComponentEvent()))));

                                    ComponentTreeUtil.raiseEvent(btn,
                                            new CComponentStack.PushComponentEvent(
                                                    new CController(list)));
                                })).TEST_NAME(p.info().getName()))
                            ._div()));
        //@formatter:on
    }

    public void addManyToOneFactory() {
        addFactory(
                p -> p.getAttribute()
                        .getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE,
                (p) -> {
                    //@formatter:off
                    return toComponentBound(
                            () -> p.proxy(),
                            html -> {
                                Object value = p.getValue();
                                html.bFormGroup()
                                    .label().content(labelUtil.getPropertyLabel(p.info().getProperty()))
                                        .fIf(value!=null, ()->html.bInputGroup())
                                        .span().BformControl().DISABLED("disabled").TEST_NAME(p.info().getName())
                                        .render(x-> crudUtil .getStrategy(IdentificationRenderer.class, p.info().getAttribute().getJavaType())
                                          .renderIdenification(html, value))
                                        ._span()
                                        .fIf(value!=null, ()->html
                                        .bInputGroupBtn().rButtonA(componentUtil.go(CrudControllerBase.class).display(value), a -> a.iconOnly())
                                            ._bInputGroupBtn()
                                            ._bInputGroup()
                                        )
                                ._bFormGroup();
                            });
                    //@formatter:on
                });
    }

    public void addNumbersFactory() {
        addFactory(p -> {
            Class<?> javaType = Primitives.wrap(p.getAttribute().getJavaType());
            return Long.class.equals(javaType) || Integer.class.equals(javaType)
                    || Short.class.equals(javaType)
                    || String.class.equals(javaType);
        } , (property) -> {
            return toComponentBound(() -> property.proxy(),
                    html -> html
                        //@formatter:off
                          .span().BformControl().DISABLED("disabled").TEST_NAME(property.info().getName())
                            .content(String.valueOf(property.getValue())));
                        //@formatter:on
        });
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.eye_open)
    void showItems(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.arrow_left)
    void back(Runnable callback) {
        callback.run();
    }
}
