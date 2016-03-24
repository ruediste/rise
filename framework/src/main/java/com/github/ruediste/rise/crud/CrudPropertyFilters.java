package com.github.ruediste.rise.crud;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.SingularAttribute;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.components.CFormGroup;
import com.github.ruediste.rise.component.components.CInput;
import com.github.ruediste.rise.component.components.CSelect;
import com.github.ruediste.rise.component.components.CText;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.components.InputType;
import com.github.ruediste.rise.component.generic.EditComponents;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.strategy.Strategies;
import com.github.ruediste.rise.core.strategy.Strategy;
import com.github.ruediste.rise.crud.CrudUtil.PersistenceFilterContext;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.lString.PatternString;
import com.github.ruediste1.i18n.lString.TranslatedString;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.MembersLabeled;
import com.github.ruediste1.i18n.message.TMessage;
import com.github.ruediste1.i18n.message.TMessages;
import com.google.common.base.Strings;
import com.google.common.primitives.Primitives;

@Singleton
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CrudPropertyFilters {

    @Inject
    Strategies strategies;

    @Inject
    LabelUtil labelUtil;

    @Inject
    ComponentFactoryUtil componentFactoryUtil;

    @Inject
    CrudReflectionUtil reflectionUtil;

    @Inject
    EditComponents editComponents;

    @TMessages
    public interface Messages {
        @TMessage("Minimum of {property}")
        PatternString minNumber(LString property);

        @TMessage("Maximum of {property}")
        PatternString maxNumber(LString property);

        TranslatedString min();

        TranslatedString max();
    }

    public interface CrudPropertyFilterFactory extends Strategy {
        Optional<CrudPropertyFilter> createFilter(CrudPropertyInfo property);
    }

    public CrudPropertyFilter create(CrudPropertyInfo info) {
        AnnotatedElement element = null;
        Member member = info.getAttribute().getJavaMember();
        if (member instanceof AnnotatedElement) {
            element = (AnnotatedElement) member;
        }

        return strategies.getStrategy(CrudPropertyFilterFactory.class).element(element).get(f -> f.createFilter(info))
                .orElseThrow(() -> new RuntimeException("No filter found for " + info));
    }

    void addFactory(Predicate<CrudPropertyInfo> filter, Function<CrudPropertyInfo, CrudPropertyFilter> factory) {
        strategies.putStrategy(CrudPropertyFilterFactory.class, info -> {
            if (filter.test(info))
                return Optional.of(factory.apply(info));
            return Optional.empty();
        });
    }

    @Inject
    Messages messages;

    /**
     * <img src="doc-files/hello.png" alt=""> t
     */
    @PostConstruct
    public void initialize() {
        addFactory(p -> String.class.equals(p.getAttribute().getJavaType()), p -> {
            PropertyInfo property = p.getProperty();
            CTextField textField = new CTextField().setText("").TEST_NAME(property.getName())
                    .setLabel(labelUtil.property(property).label());
            CFormGroup component = new CFormGroup(textField);
            return new CrudPropertyFilter() {

                @Override
                public Component getComponent() {
                    return component;
                }

                @Override
                public void applyFilter(PersistenceFilterContext ctx) {
                    CriteriaBuilder cb = ctx.cb();
                    Path<String> path = ctx.root().get((SingularAttribute) p.getAttribute());
                    ctx.addWhere(cb.or(path.isNull(), cb.like(path, "%" + textField.getText() + "%")));
                }
            };
        });
        addNumberFactory(Long.class, Long::parseLong);
        addNumberFactory(Integer.class, Integer::parseInt);
        addNumberFactory(Short.class, Short::parseShort);
        addBooleanFactory();
        addDurationFactory();
        addZoneIdFactory();
        addLocalTimeFactory();
    }

    private void addLocalTimeFactory() {
        // TODO Auto-generated method stub

        throw new UnsupportedOperationException();
    }

    private void addZoneIdFactory() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();

    }

    private void addDurationFactory() {
        throw new UnsupportedOperationException();
    }

    private Predicate<CrudPropertyInfo> isOfType(Class<?> cls) {
        return p -> cls.equals(Primitives.wrap(p.getAttribute().getJavaType()));
    }

    @MembersLabeled
    enum EnumChoices {
        NULL(null), TRUE(true), FALSE(false);
        final Boolean value;

        private EnumChoices(Boolean value) {
            this.value = value;
        }
    }

    private void addBooleanFactory() {
        addFactory(isOfType(Boolean.class), p -> {
            SingularAttribute<?, ?> singularAttribute = (SingularAttribute<?, ?>) p.getAttribute();
            List<EnumChoices> choices = new ArrayList<>();
            if (singularAttribute.isOptional())
                choices.add(EnumChoices.NULL);
            choices.addAll(Arrays.asList(EnumChoices.TRUE, EnumChoices.FALSE));

            CSelect<EnumChoices> select = new CSelect<EnumChoices>().setItems(choices).setAllowEmpty(true)
                    .setChildComponentFactory(choice -> new CText(labelUtil.enumMember(choice).label()));
            return new CrudPropertyFilter() {

                @Override
                public Component getComponent() {
                    return select;
                }

                @Override
                public void applyFilter(PersistenceFilterContext<?> ctx) {
                    select.getSelectedItem()
                            .ifPresent(choice -> ctx.addWhere(ctx.cb().equal(ctx.root().get(p.getName()), choice)));
                }
            };
        });

    }

    private <T extends Comparable> void addNumberFactory(Class<T> boxCls, Function<String, T> parse) {

        addFactory(isOfType(boxCls), p -> {
            PropertyInfo property = p.getProperty();
            LString propertyLabel = labelUtil.property(property).label();
            CInput min = new CInput(InputType.number).setLabel(messages.minNumber(propertyLabel)).setValue("")
                    .setRenderFormGroup(false);

            CInput max = new CInput(InputType.number).setLabel(messages.maxNumber(propertyLabel)).setValue("")
                    .setRenderFormGroup(false);

            // @formatter:off
                        Component component = componentFactoryUtil.toComponent((BootstrapRiseCanvas<?> html) ->
                          html
                          .bFormGroup()
                            .label().content(labelUtil.property(property).label())
                              .div().BformInline()
                                .bInputGroup().CLASS(x->x.sm(6))
                                  .span().BinputGroupAddon().content(messages.min())
                                  .add(min)
                                ._bInputGroup()
                                .bInputGroup().CLASS(x->x.sm(6))
                                  .span().BinputGroupAddon().content(messages.max())
                                  .add(max)
                                ._bInputGroup()
                            ._div()
                          ._bFormGroup());
                        // @formatter:on

            return new CrudPropertyFilter() {

                @Override
                public Component getComponent() {
                    return component;
                }

                @Override
                public void applyFilter(PersistenceFilterContext ctx) {
                    if (!Strings.isNullOrEmpty(min.getValue())) {
                        ctx.addWhere(ctx.cb().greaterThanOrEqualTo(ctx.root().get(property.getName()),
                                parse.apply(min.getValue())));

                    }
                    if (!Strings.isNullOrEmpty(max.getValue())) {
                        ctx.addWhere(ctx.cb().lessThanOrEqualTo(ctx.root().get(property.getName()),
                                parse.apply(max.getValue())));

                    }

                }
            };
        });
    }

}
