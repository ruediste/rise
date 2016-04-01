package com.github.ruediste.rise.crud;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
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
import com.github.ruediste.rise.component.generic.DisplayRenderer;
import com.github.ruediste.rise.component.generic.DisplayRenderers;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentTreeUtil;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.core.strategy.Strategies;
import com.github.ruediste.rise.core.strategy.Strategy;
import com.github.ruediste.rise.crud.CrudUtil.CrudList;
import com.github.ruediste.rise.crud.CrudUtil.IdentificationRenderer;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;

@Singleton
public class CrudDisplayComponents {

    @Inject
    Strategies strategies;

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

    private Component toComponentBound(Supplier<?> bindingAccessor, Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponentBound(bindingAccessor, renderer);
    }

    private Component toComponent(Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponent(renderer);
    }

    public interface CrudDisplayComponentFactory extends Strategy {
        Optional<Component> create(CrudPropertyHandle property);
    }

    public Component create(CrudPropertyHandle propertyHandle) {
        AnnotatedElement element = null;
        Member member = propertyHandle.info().getAttribute().getJavaMember();
        if (member instanceof AnnotatedElement) {
            element = (AnnotatedElement) member;
        }

        return strategies.getStrategy(CrudDisplayComponentFactory.class).element(element)
                .get(f -> f.create(propertyHandle))
                // .map(c -> toComponent(html -> html.bFormGroup().label()
                // .content(labelUtil.property(propertyHandle.info().getProperty()).label()).add(c)._bFormGroup()))
                .orElseThrow(() -> new RuntimeException("No display component found for " + propertyHandle));
    }

    void addFactory(Predicate<CrudPropertyInfo> filter, Function<CrudPropertyHandle, Component> factory) {
        strategies.putStrategy(CrudDisplayComponentFactory.class, handle -> {
            if (filter.test(handle.info()))
                return Optional.of(factory.apply(handle));
            return Optional.empty();
        });
    }

    @Inject
    DisplayRenderers displayRenderers;

    @PostConstruct
    public void initialize() {
        addEnumElementCollectionFactory();
        addManyToOneFactory();
        addOneToManyFactory();
        addEmbeddedFactory();
        addDisplayRendererFactory();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void addDisplayRendererFactory() {
        strategies.putStrategy(CrudDisplayComponentFactory.class, handle -> {
            return displayRenderers.property(handle.info().getProperty()).tryGet()
                    .map(renderer -> toComponentBound(() -> handle.proxy(), html -> {
                        html.span().BformControl().DISABLED("disabled").TEST_NAME(handle.info().getName());
                        ((DisplayRenderer) renderer).render(html, handle.getValue());
                        html._span();
                    }));
        });
    }

    public void addEmbeddedFactory() {
        addFactory(p -> p.getAttribute().getPersistentAttributeType() == PersistentAttributeType.EMBEDDED,
                handle -> toComponentBound(() -> handle.proxy(), html -> {

                    Object value = handle.getValue();
                    if (value == null) {
                        Class<?> rawType = handle.info().getProperty().getPropertyType().getRawType();
                        try {
                            value = rawType.newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException("Error while instantiating " + rawType + " for embeddable "
                                    + handle.info().getProperty() + " which was null");
                        }
                    }
                    Object finalValue = value;
                    html.bCol(x -> x.xs(11).xsOffset(1));
                    for (CrudPropertyInfo property : crudReflectionUtil.getDisplayProperties(
                            crudReflectionUtil.getPersistentType(handle.info().getEmQualifier(), value.getClass()))) {
                        CrudPropertyHandle subHandle = CrudPropertyHandle.create(property, handle::rootEntity,
                                () -> finalValue, handle.group());
                        html.bFormGroup().label().content(labelUtil.property(subHandle.info().getProperty()).label())
                                .add(create(subHandle))._bFormGroup();
                    }
                    html._bCol();
                }));
    }

    public void addEnumElementCollectionFactory() {
        addFactory(p -> p.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ELEMENT_COLLECTION,
                (p) -> toComponentBound(() -> p.proxy(), html -> {
                    html.div().content(StreamSupport.stream(((Iterable<?>) p.getValue()).spliterator(), false)
                            .map(String::valueOf).collect(Collectors.joining(", ")));
                }));
    }

    @SuppressWarnings("rawtypes")
    public void addOneToManyFactory() {
        // @formatter:off
		addFactory(p -> p.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_MANY,
				(p) -> toComponent(html -> html.div().add(new CButton(this, (btn, x) -> x.showItems(() -> {
					PluralAttribute attr = (PluralAttribute) p.info().getAttribute();
					Class<?> elementType = attr.getElementType().getJavaType();
					CrudList list = crudUtil.getStrategy(CrudUtil.CrudListFactory.class, elementType)
							.createList(p.info().getEmQualifier(), elementType, ctx -> {
								ctx.addWhere(ctx.root().in((Collection) p.getValue()));
							});
					list.setItemActionsFactory(obj -> new CDataGrid.Cell(
							new CButton(componentUtil.go(CrudControllerBase.class).display(obj))));
					list.setBottomActions(new CButton(this, x1 -> x1
							.back(() -> ComponentTreeUtil.raiseEvent(btn, new CComponentStack.PopComponentEvent()))));

					ComponentTreeUtil.raiseEvent(btn, new CComponentStack.PushComponentEvent(new CController(list)));
				})).TEST_NAME(p.info().getName()))._div()));
		// @formatter:on
    }

    public void addManyToOneFactory() {
        addFactory(p -> p.getAttribute().getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE, (p) -> {
            // @formatter:off
			return toComponentBound(() -> p.proxy(), html -> {
				Object value = p.getValue();
				html.fIf(value != null, () -> html.bInputGroup()).span().BformControl().DISABLED("disabled")
						.TEST_NAME(p.info().getName())
						.render(x -> crudUtil
								.getStrategy(IdentificationRenderer.class, p.info().getAttribute().getJavaType())
								.renderIdenification(html, value))
						._span()
						.fIf(value != null, () -> html.bInputGroupBtn()
								.rButtonA(componentUtil.go(CrudControllerBase.class).display(value), a -> a.iconOnly())
								._bInputGroupBtn()._bInputGroup());
			});
			// @formatter:on
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
