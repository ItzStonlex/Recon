package org.itzstonlex.recon.http.app;

import org.itzstonlex.recon.http.app.util.PathLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpContextPath {

    String baseDir() default "";

    String context() default "/";

    String contentPath() default "/index.html";

    PathLevel level() default PathLevel.CLASSPATH;
}
