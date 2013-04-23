package me.shakiba.jdbi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
@BindingAnnotation(BindAnnoFactory.class)
public @interface BindAnno {
    String value() default "___jdbi_bare___";
}
