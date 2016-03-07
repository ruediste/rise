package com.github.ruediste.rise.crud;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvasCss.B_ButtonStyle;
import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.api.ButtonStyle;
import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.crud.CrudUtil.BrowserFactory;
import com.github.ruediste.rise.crud.CrudUtil.CreateFactory;
import com.github.ruediste.rise.crud.CrudUtil.DeleteFactory;
import com.github.ruediste.rise.crud.CrudUtil.DisplayFactory;
import com.github.ruediste.rise.crud.CrudUtil.EditFactory;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.Labeled;

/**
 * Base class for controllers exposing the CRUD UI.
 * 
 * <p>
 * This class is intended to be subclassed in the concrete applications.
 */
public abstract class CrudControllerBase extends ControllerComponent {

    @Inject
    CrudUtil crudUtil;

    public static class Data {
        private Object subController;

        public Object getSubController() {
            return subController;
        }

        public void setSubController(Object subController) {
            this.subController = subController;
        }
    }

    @Inject
    private BindingGroup<Data> data;

    public Data data() {
        return data.proxy();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.list)
    public ActionResult browse(Class<?> entityClass, Class<? extends Annotation> emQualifier) {
        data.get().setSubController(
                crudUtil.getStrategy(BrowserFactory.class, entityClass).createBrowser(entityClass, emQualifier));
        return null;
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.eye_open)
    @ButtonStyle(B_ButtonStyle.PRIMARY)
    public ActionResult display(Object entity) {
        data.get()
                .setSubController(crudUtil.getStrategy(DisplayFactory.class, entity.getClass()).createDisplay(entity));
        return null;
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.edit)
    public ActionResult edit(Object entity) {
        data.get().setSubController(crudUtil.getStrategy(EditFactory.class, entity.getClass()).createEdit(entity));
        return null;
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.plus_sign)
    public ActionResult create(Class<?> entityClass, Class<? extends Annotation> emQualifier) {
        data.get().setSubController(
                crudUtil.getStrategy(CreateFactory.class, entityClass).createCreate(entityClass, emQualifier));
        return null;
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.remove_sign)
    @ButtonStyle(B_ButtonStyle.DANGER)
    public ActionResult delete(Object entity) {
        data.get().setSubController(crudUtil.getStrategy(DeleteFactory.class, entity.getClass()).createDelete(entity));
        return null;
    }
}
