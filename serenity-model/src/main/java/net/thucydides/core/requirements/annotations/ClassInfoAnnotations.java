package net.thucydides.core.requirements.annotations;

import com.google.common.reflect.ClassPath;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ClassInfoAnnotations {
        private final ClassPath.ClassInfo classInfo;

        public ClassInfoAnnotations(ClassPath.ClassInfo classInfo) {

            this.classInfo = classInfo;
        }

        public static ClassInfoAnnotations theClassDefinedIn(ClassPath.ClassInfo classInfo)   {
             return new ClassInfoAnnotations(classInfo);
         }

        public boolean hasAnAnnotation(Class<? extends Annotation>... annotationClasses) {
            for(Class<? extends Annotation> annotationClass : annotationClasses) {
                if (classInfo.load().getAnnotation(annotationClass) != null) {
                    return true;
                }
            }
            return false;
        }

    public boolean hasAPackageAnnotation(Class<? extends Annotation>... annotationClasses) {
        for(Class<? extends Annotation> annotationClass : annotationClasses) {
            if (classInfo.load().getPackage().getAnnotation(annotationClass) != null) {
                return true;
            }
        }
        return false;
    }

    public boolean containsTests() {
        for (Method method : classInfo.load().getMethods()) {
            if (method.getAnnotation(org.junit.Test.class) != null) {
                return true;
            }
        }
        return false;
    }
}