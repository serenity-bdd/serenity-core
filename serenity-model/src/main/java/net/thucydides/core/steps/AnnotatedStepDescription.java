package net.thucydides.core.steps;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.annotations.*;
import net.thucydides.core.reflection.MethodFinder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.thucydides.core.util.NameConverter.humanize;

/**
 * Test steps and step groups can be described by various annotations.
 */
public final class AnnotatedStepDescription {

    private static final List<String> VALID_STEP_ANNOTATIONS = NewList.of("Step", "Given", "When", "Then");

    private final ExecutedStepDescription description;

    public static AnnotatedStepDescription from(final ExecutedStepDescription description) {
        return new AnnotatedStepDescription(description);

    }

    private AnnotatedStepDescription(final ExecutedStepDescription description) {
        this.description = description;
    }

    public List<String> getAnnotatedRequirements() {
        List<String> requirements = new ArrayList<>();
        Method testMethod = getTestMethod();
        if (testMethod != null) {
            addRequirementFrom(requirements, testMethod);
            addMultipleRequirementsFrom(requirements, testMethod);
        }
        return requirements;
    }

    private void addMultipleRequirementsFrom(final List<String> requirements, final Method testMethod) {
        TestsRequirements testRequirements = testMethod.getAnnotation(TestsRequirements.class);
        if (testRequirements != null) {
            requirements.addAll(Arrays.asList(testRequirements.value()));
        }
    }

    private void addRequirementFrom(final List<String> requirements, final Method testMethod) {
        TestsRequirement testsRequirement = testMethod
                .getAnnotation(TestsRequirement.class);
        if (testsRequirement != null) {
            requirements.add(testsRequirement.value());
        }
    }

//    public Method getTestMethod() {
//        if (getTestClass() != null) {
//            return methodCalled(description, getTestClass());
//        } else {
//            return null;
//        }
//    }
    public Method getTestMethod() {
        if (getTestClass() != null) {
            if (ScreenplayInspector.isAScreenplayClass(getTestClass())) {
                return ScreenplayInspector.performAsMethodIn(getTestClass());
            } else {
                return methodCalled(description, getTestClass());
            }
        } else {
            return null;
        }
    }

    public Method getTestMethodIfPresent() {
        return findMethodCalled(description, getTestClass());
    }

    private String withNoArguments(final String methodName) {
        String unqualifiedName = unqualified(methodName);
        return stripFrom(':', unqualifiedName);
    }

    private String stripFrom(char boundaryChar, String text) {
        int boundaryPosition = text.indexOf(boundaryChar);
        if (boundaryPosition > 0) {
            return text.substring(0, boundaryPosition);
        } else {
            return text;
        }
    }

    private String unqualified(String methodName) {
        return StringUtils.stripStart(methodName,"[0123456789] ");
    }

    private Class<?> getTestClass() {
        return description.getStepClass();
    }

    private Method methodCalled(final ExecutedStepDescription methodName, final Class<?> testClass) {
        Method methodFound = findMethodCalled(methodName, testClass);
        if (methodFound == null) {
            throw new IllegalArgumentException("No test method called " + methodName + " was found in " + testClass);
        }
        return methodFound;
    }

    private Method findMethodCalled(final ExecutedStepDescription method, final Class<?> testClass) {
        return MethodFinder.inClass(testClass).getMethodNamed(withNoArguments(method.getName()), method.getArguments().size());
    }

    public String getAnnotatedTitle() {

        Method testMethod = getTestMethod();
        Title title = testMethod.getAnnotation(Title.class);
        if (title != null) {
            return title.value();
        }
        return null;
    }

    private Optional<String> getAnnotatedStepName() {
        Optional<String> stepAnnotatedName = getNameFromStepAnnotationIn(getTestMethod());
        if (stepAnnotatedName.isPresent()) {
            return stepAnnotatedName;
        } else {
            return getCompatibleStepNameFrom(getTestMethod());
        }
    }

    public static boolean isACompatibleStep(Annotation annotation) {
        return VALID_STEP_ANNOTATIONS.contains(annotation.annotationType().getSimpleName());
    }

    private Optional<String> getCompatibleStepNameFrom(Method testMethod) {
        Annotation[] annotations = testMethod.getAnnotations();
        for (Annotation annotation : annotations) {
            if (isACompatibleStep(annotation)) {
                try {
                    String annotationType = annotation.annotationType().getSimpleName();
                    String annotatedValue = (String)annotation.getClass().getMethod("value").invoke(annotation);
                    if (StringUtils.isEmpty(annotatedValue)) {
                        return Optional.empty();
                    } else {
                        return Optional.of(annotationType + " " + StringUtils.uncapitalize(annotatedValue));
                    }

                } catch (Exception ignoredException) {}
            }
        }
        return Optional.empty();
    }

    private Optional<String> getNameFromStepAnnotationIn(final Method testMethod) {
        Step step = testMethod.getAnnotation(Step.class);

        if ((step != null) && (!StringUtils.isEmpty(step.value()))) {
            return Optional.of(injectAnnotatedFieldValuesFrom(description).into(step.value()));
        }
        return Optional.empty();
    }

    private AnnotatedFieldValuesBuilder injectAnnotatedFieldValuesFrom(ExecutedStepDescription description) {
        return new AnnotatedFieldValuesBuilder(description);
    }

    private  class AnnotatedFieldValuesBuilder {
        private final ExecutedStepDescription description;

        private AnnotatedFieldValuesBuilder(ExecutedStepDescription description) {
            this.description = description;
        }

        public String into(String stepDescription) {

            stepDescription = resolveMetaFieldIfPresentIn(stepDescription);
            Map<String, Object> fields = description.getDisplayedFields();
            for(String field : fields.keySet()) {
                stepDescription = ReplaceField.in(stepDescription).theFieldCalled(field).with(fields.get(field));
            }
            return stepDescription;
        }

        private String resolveMetaFieldIfPresentIn(String stepDescription) {

            if (!MetaField.from(stepDescription).isDefined()) {
                return stepDescription;
            }

            String metafield = MetaField.from(stepDescription).template();

            String metafieldName = MetaField.from(stepDescription).fieldName();

            Optional<String> matchingField = description.getDisplayedFields().keySet().stream()
                                            .filter( key -> key.equals(metafieldName))
                                            .findFirst();

            Map<String,Object> displayedFields = new HashMap<>(description.getDisplayedFields());

            if (matchingField.isPresent()) {
                String field = matchingField.get();
                stepDescription = ReplaceField.in(metafield).theFieldCalled(field).with(displayedFields.get(field));
                displayedFields.remove(field);
            }

            return stepDescription;
        }
    }

    public String getName() {
        if (noClassIsDefined()) {
            return description.getName();
        } else if (isAGroup()) {
            return groupName();
        } else {
            return stepName();
        }
    }

    private boolean noClassIsDefined() {
        return description.getStepClass() == null;
    }

    private String groupName() {
        String annotatedGroupName = getGroupName();
        if (!StringUtils.isEmpty(annotatedGroupName)) {
            return annotatedGroupName;
        } else {
            return stepName();
        }
    }

    private String stepName() {
        String annotationTitle = getAnnotatedTitle();
        if (!StringUtils.isEmpty(annotationTitle)) {
            return annotationTitle;
        }

        Optional<String> annotatedStepName = getAnnotatedStepName();
        if (getAnnotatedStepName().isPresent() && (StringUtils.isNotEmpty(annotatedStepName.get()))) {
            return annotatedStepNameWithParameters(annotatedStepName.get());
        }

        return humanize(description.getName());
    }


    private String annotatedStepNameWithParameters(String annotatedStepTemplate) {
        String annotatedStepName = annotatedStepTemplate;

        for (int i = 0; i < description.getRawArguments().size(); i++) {
            Pattern tokenPattern = Pattern.compile(String.format("\\{%d(\\.[^}]+)?\\}", i));
            Matcher tokenMatcher = tokenPattern.matcher(annotatedStepName);

            List<String> tokens = new ArrayList<>();
            List<String> tokenDetails = new ArrayList<>();

            //it could be several references to the same parameter
            while (tokenMatcher.find()) {
                String token = tokenMatcher.group();
                if (!tokens.contains(token)) {
                    tokens.add(token);
                    String tokenDetail = tokenMatcher.group(1);
                    tokenDetails.add(tokenDetail);
                }
            }

            Object argument = description.getRawArguments().get(i);

            for (int j = 0; j < tokens.size(); j++) {
                String replacement;

                //if there are some additional details of the parameter, they should be processed
                if (tokenDetails.get(j) != null) {
                    replacement = getReplacementForArgument(argument, tokenDetails.get(j));
                }
                //otherwise, replace based on toString() representation of parameter
                else {
                    replacement = description.getArguments().get(i);
                }

                annotatedStepName = StringUtils.replace(annotatedStepName, tokens.get(j), replacement);
            }
        }

        return annotatedStepName;
    }

    private String getReplacementForArgument(Object argument, String tokenDetail)  {
        List<String> references = Stream.of(tokenDetail.split("\\."))
                .filter(e -> !e.isEmpty())
                .map(String::trim)
                .collect(Collectors.toList());

        Object resolvedObject = argument;

        for (String reference : references) {
            boolean isMethod = isMethod(reference);
            if (isMethod) {
                resolvedObject = getMethodResult(resolvedObject, reference);
            }
            else {
                resolvedObject = getFieldValue(resolvedObject, reference);
            }
        }

        return String.valueOf(resolvedObject);
    }

    private Object getMethodResult(Object o, String methodName) {
        if (o == null) {
            return null;
        }

        String cleanedMethodName = methodName.replace("()", "").trim();

        try {
            return MethodUtils.invokeMethod(o, cleanedMethodName);
        } catch (NoSuchMethodException|IllegalAccessException| InvocationTargetException e) {
            return String.valueOf(o);
        }
    }

    private Object getFieldValue(Object o, String fieldName) {
        if (o == null) {
            return null;
        }
        try {
            return FieldUtils.readField(o, fieldName, true);
        } catch (IllegalAccessException|IllegalArgumentException e) {
            return String.valueOf(o);
        }
    }

    private boolean isMethod(String reference) {
        return reference.endsWith("()");
    }

    public boolean isAGroup() {

        Method testMethod = getTestMethodIfPresent();
        if (testMethod != null) {
            StepGroup testGroup = testMethod.getAnnotation(StepGroup.class);
            return (testGroup != null);
        } else {
            return false;
        }
    }

    private String getGroupName() {
        Method testMethod = getTestMethodIfPresent();
        StepGroup testGroup = testMethod.getAnnotation(StepGroup.class);
        return testGroup.value();
    }

    public boolean isPending() {
        Method testMethod = getTestMethodIfPresent();
        return testMethod != null && TestStatus.of(testMethod).isPending();
    }

    public boolean isIgnored() {
        Method testMethod = getTestMethodIfPresent();
        return testMethod != null && TestStatus.of(testMethod).isIgnored();
    }

}
