package org.itzstonlex.recon.annotation;

import org.itzstonlex.recon.annotation.type.BindServer;
import org.itzstonlex.recon.annotation.type.ConnectClient;
import org.itzstonlex.recon.annotation.type.Property;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class AnnotatedReconScanner {

    private static final class InstanceScanner {

        private final Object instance;

        public InstanceScanner(Object instance) {
            this.instance = instance;
        }

        public void handle(AnnotationHandler annotationHandler)
        throws Exception {

            processFields(annotationHandler);
            processConstructors(annotationHandler);
            processClasses(annotationHandler);
        }

        public void processFields(AnnotationHandler annotationHandler)
        throws Exception {

            for (Field field : instance.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Property.class)) {
                    annotationHandler.handle(field.getAnnotation(Property.class), instance, field);
                }

                if (field.isAnnotationPresent(ConnectClient.class)) {
                    annotationHandler.handle(field.getAnnotation(ConnectClient.class), instance, field);
                }

                if (field.isAnnotationPresent(BindServer.class)) {
                    annotationHandler.handle(field.getAnnotation(BindServer.class), instance, field);
                }
            }
        }

        public void processConstructors(AnnotationHandler annotationHandler) {
            // ...
        }

        public void processClasses(AnnotationHandler annotationHandler) {
            // ...
        }
    }


    private static final List<ReconProperty> propertyList = new ArrayList<>();

    public static void addProperty(ReconProperty reconProperty) {
        propertyList.add(reconProperty);
    }

    public static List<ReconProperty> getPropertyList() {
        return propertyList;
    }

    public static void scanInstance(Object instance) {
        InstanceScanner instanceScanner = new InstanceScanner(instance);
        AnnotationHandler annotationHandler = new AnnotationHandler();

        try {
            instanceScanner.handle(annotationHandler);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
