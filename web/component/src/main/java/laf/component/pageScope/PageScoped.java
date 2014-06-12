package laf.component.pageScope;

import java.lang.annotation.*;

import javax.enterprise.context.NormalScope;

@NormalScope
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
public @interface PageScoped {

}
