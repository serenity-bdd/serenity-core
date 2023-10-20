package net.serenitybdd.core.annotations.locators;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.*;
import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.annotations.findby.How;
import net.serenitybdd.core.annotations.findby.di.CustomFindByAnnotationProviderService;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.thucydides.core.webdriver.MobilePlatform;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.ByChained;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static io.appium.java_client.remote.MobilePlatform.ANDROID;
import static io.appium.java_client.remote.MobilePlatform.IOS;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;


public class SmartAnnotations extends Annotations {

    private Field field;
    private MobilePlatform platform;
    private CustomFindByAnnotationProviderService customFindByAnnotationProviderService;

    private final static Class<?>[] DEFAULT_ANNOTATION_METHOD_ARGUMENTS = new Class<?>[]{};

    private enum Strategies {
        BYUIAUTOMATOR("uiAutomator") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                String value = getValue(annotation, this);
                if (annotation.annotationType().equals(AndroidFindBy.class)) {
                    return AppiumBy.androidUIAutomator(value);
                }
//                if (annotation.annotationType().equals(iOSXCUITFindBy.class)) {
//                    return MobileBy.IosUIAutomation(value);
//                }
                return super.getBy(annotation);
            }
        },
        BYACCESSABILITY("accessibility") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                return AppiumBy.accessibilityId(getValue(annotation, this));
            }
        },
        BYCLASSNAME("className") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                return By.className(getValue(annotation, this));
            }
        },
        BYID("id") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                return By.id(getValue(annotation, this));
            }
        },
        BYTAG("tagName") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                return By.tagName(getValue(annotation, this));
            }
        },
        BYNAME("name") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                return By.name(getValue(annotation, this));
            }
        },
        BYXPATH("xpath") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                return By.xpath(getValue(annotation, this));
            }
        },
        BYCSS("css") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                return By.cssSelector(getValue(annotation, this));
            }
        },
        BYLINKTEXT("linkText") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                return By.linkText(getValue(annotation, this));
            }
        },
        BYPARTIALLINKTEXT("partialLinkText") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                return By.partialLinkText(getValue(annotation, this));
            }
        },
        JQUERY("jQuery") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                return By.jquery(getValue(annotation, this));
            }
        },
        SCUSING("scUsing") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                return By.sclocator(getValue(annotation, this));
            }
        },
        BYIOSCLASSCHAIN("iOSClassChain") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                return AppiumBy.iOSClassChain(getValue(annotation, this));
            }
        },
        BYIOSNSPREDICATE("iOSNsPredicate") {
            @Override
            org.openqa.selenium.By getBy(Annotation annotation) {
                return AppiumBy.iOSNsPredicateString(getValue(annotation, this));
            }
        };

        private final String valueName;

        private String returnValueName() {
            return valueName;
        }

        private Strategies(String valueName) {
            this.valueName = valueName;
        }

        private static String[] strategyNames() {
            Strategies[] strategies = values();
            String[] result = new String[strategies.length];
            int i = 0;
            for (Strategies strategy : values()) {
                result[i] = strategy.valueName;
                i++;
            }
            return result;
        }

        private static String getValue(Annotation annotation,
                                       Strategies strategy) {
            try {
                Method m = annotation.getClass().getMethod(strategy.valueName, DEFAULT_ANNOTATION_METHOD_ARGUMENTS);
                return m.invoke(annotation, new Object[]{}).toString();
            } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        org.openqa.selenium.By getBy(Annotation annotation) {
            return null;
        }
    }

    public SmartAnnotations(Field field, MobilePlatform platform) {
//        this(field, platform, WebDriverInjectors.getInjector().getInstance(CustomFindByAnnotationProviderService.class));
        this(field, platform, SerenityInfrastructure.getCustomFindByAnnotationProviderService());
    }

    public SmartAnnotations(Field field, MobilePlatform platform,
                            CustomFindByAnnotationProviderService customFindByAnnotationProviderService) {
        super(field);
        this.field = field;
        this.platform = platform;
        this.customFindByAnnotationProviderService = customFindByAnnotationProviderService;
    }

    protected void assertValidAnnotations() {
        FindBys findBys = field.getAnnotation(FindBys.class);
        FindBy myFindBy = field.getAnnotation(FindBy.class);

        // START - [dep1] *** todo [deprecate thucydides] Remove this check once thucydides package name removed
        net.thucydides.core.annotations.findby.FindBy myDepFindBy = field.getAnnotation(net.thucydides.core.annotations.findby.FindBy.class);
        if (myDepFindBy != null && myFindBy != null) {
            throw new IllegalArgumentException(("Do not combine the serenitybdd and thucydides namespace. The thucydides namespace is now deprecated, so please convert."));
        }

        if (findBys != null && myDepFindBy != null) {
            throw new IllegalArgumentException("If you use a '@FindBys' annotation, "
                    + "you must not also use a '@FindBy' annotation");
        }
        // END - [dep1] *** [deprecated thucydides]

        if (findBys != null && myFindBy != null) {
            throw new IllegalArgumentException("If you use a '@FindBys' annotation, "
                    + "you must not also use a '@FindBy' annotation");
        }
    }

    //	@Override
    public org.openqa.selenium.By buildBy() {
        assertValidAnnotations();

        org.openqa.selenium.By by = null;

        // appium additions
        AndroidFindBy androidBy = field.getAnnotation(AndroidFindBy.class);
        if (androidBy != null && ANDROID.toUpperCase().equals(platform.name())) {
            by = getMobileBy(androidBy, getFieldValue(androidBy));
        }

        AndroidFindBys androidBys = field.getAnnotation(AndroidFindBys.class);
        if (androidBys != null && ANDROID.toUpperCase().equals(platform.name())) {
            by = getComplexMobileBy(androidBys.value(), ByChained.class);
        }

        AndroidFindAll androidFindAll = field.getAnnotation(AndroidFindAll.class);
        if (androidFindAll != null && ANDROID.toUpperCase().equals(platform.name())) {
            by = getComplexMobileBy(androidFindAll.value(), ByChained.class);
        }

        iOSXCUITFindBy iOSBy = field.getAnnotation(iOSXCUITFindBy.class);
        if (iOSBy != null && IOS.toUpperCase().equals(platform.name())) {
            by = getMobileBy(iOSBy, getFieldValue(iOSBy));
        }

        iOSXCUITFindBys iOSBys = field.getAnnotation(iOSXCUITFindBys.class);
        if (iOSBys != null && IOS.toUpperCase().equals(platform.name())) {
            by = getComplexMobileBy(iOSBys.value(), ByChained.class);
        }

        iOSXCUITFindAll iOSXCUITFindAll = field.getAnnotation(iOSXCUITFindAll.class);
        if (iOSXCUITFindAll != null && IOS.toUpperCase().equals(platform.name())) {
            by = getComplexMobileBy(iOSXCUITFindAll.value(), ByChained.class);
        }

        //my additions to FindBy
        FindBy myFindBy = field.getAnnotation(FindBy.class);
        if (by == null && myFindBy != null) {
            by = buildByFromFindBy(myFindBy);
        }

        // START - [deo2] *** todo [deprecate thucydides] Remove this code once thucydides package name removed
        //my additions to FindBy
        net.thucydides.core.annotations.findby.FindBy myDepFindBy = field.getAnnotation(net.thucydides.core.annotations.findby.FindBy.class);
        if (by == null && myDepFindBy != null) {
            by = buildByFromFindBy(myDepFindBy);
        }
        //END - [dep2] ***

//        FindBys thucydidesBys = field.getAnnotation(FindBys.class);
//        if (thucydidesBys != null) {
//            by = buildByFromFindBys(thucydidesBys);
//        }
//
//        // default implementation in case if org.openqa.selenium.support.FindBy was used
//        org.openqa.selenium.support.FindBy seleniumBy = field.getAnnotation(org.openqa.selenium.support.FindBy.class);
//        if (seleniumBy != null) {
//            by = super.buildByFromFindBy(seleniumBy);
//        }
//
//        org.openqa.selenium.support.FindAll seleniumFindAll = field.getAnnotation(org.openqa.selenium.support.FindAll.class);
//        if (seleniumFindAll != null) {
//            by = super.buildBysFromFindByOneOf(seleniumFindAll);
//        }
//
        if (by == null) {
            by = buildByFromCustomAnnotationProvider(field);
        }

        if (by == null) {
            by = super.buildBy();
        }

        if (by == null) {
            by = buildByFromDefault();
        }

        if (by == null) {
            throw new IllegalArgumentException("Cannot determine how to locate element " + field);
        }

        return by;
    }

    protected org.openqa.selenium.By buildByFromCustomAnnotationProvider( Field field) {
        return customFindByAnnotationProviderService.getCustomFindByAnnotationServices().stream()
                .filter(annotationService ->annotationService.isAnnotatedByCustomFindByAnnotation(field))
                .findFirst().map(annotationService ->annotationService.buildByFromCustomFindByAnnotation(field))
                .orElse(null);
    }

    protected org.openqa.selenium.By buildByFromFindBy(FindBy myFindBy) {
        assertValidFindBy(myFindBy);

        org.openqa.selenium.By by = buildByFromShortFindBy(myFindBy);
        if (by == null) {
            by = buildByFromLongFindBy(myFindBy);
        }

        return by;
    }

    protected org.openqa.selenium.By buildByFromLongFindBy(FindBy myFindBy) {
        How how = myFindBy.how();
        String using = myFindBy.using();

        switch (how) {
            case CLASS_NAME:
                return org.openqa.selenium.By.className(using);

            case CSS:
                return org.openqa.selenium.By.cssSelector(using);

            case ID:
                return org.openqa.selenium.By.id(using);

            case ID_OR_NAME:
                return new ByIdOrName(using);

            case LINK_TEXT:
                return org.openqa.selenium.By.linkText(using);

            case NAME:
                return org.openqa.selenium.By.name(using);

            case PARTIAL_LINK_TEXT:
                return org.openqa.selenium.By.partialLinkText(using);

            case TAG_NAME:
                return org.openqa.selenium.By.tagName(using);

            case XPATH:
                return org.openqa.selenium.By.xpath(using);

            case JQUERY:
                return By.jquery(using);

            case SCLOCATOR:
                return By.sclocator(using);

            case ACCESSIBILITY_ID:
                return AppiumBy.accessibilityId(using);

//            case IOS_UI_AUTOMATION:
//                return MobileBy.IosUIAutomation(using);

            case ANDROID_UI_AUTOMATOR:
                return AppiumBy.androidUIAutomator(using);

            case IOS_CLASS_CHAIN:
                return AppiumBy.iOSClassChain(using);

            case IOS_NS_PREDICATE_STRING:
                return AppiumBy.iOSNsPredicateString(using);

            default:
                // Note that this shouldn't happen (eg, the above matches all
                // possible values for the How enum)
                throw new IllegalArgumentException("Cannot determine how to locate element " + field);
        }
    }

    protected org.openqa.selenium.By buildByFromShortFindBy(FindBy myFindBy) {
        if (isNotEmpty(myFindBy.className())) {
            return org.openqa.selenium.By.className(myFindBy.className());
        }
        if (isNotEmpty(myFindBy.css())) {
            return org.openqa.selenium.By.cssSelector(myFindBy.css());
        }
        if (isNotEmpty(myFindBy.id())) {
            return org.openqa.selenium.By.id(myFindBy.id());
        }
        if (isNotEmpty(myFindBy.linkText())) {
            return org.openqa.selenium.By.linkText(myFindBy.linkText());
        }
        if (isNotEmpty(myFindBy.name())) {
            return org.openqa.selenium.By.name(myFindBy.name());
        }
        if (isNotEmpty(myFindBy.ngModel())) {
            return org.openqa.selenium.By.cssSelector("*[ng-model='" + myFindBy.ngModel() + "']");
        }

        if (isNotEmpty(myFindBy.partialLinkText())) {
            return org.openqa.selenium.By.partialLinkText(myFindBy.partialLinkText());
        }
        if (isNotEmpty(myFindBy.tagName())) {
            return org.openqa.selenium.By.tagName(myFindBy.tagName());
        }

        if (isNotEmpty(myFindBy.xpath())) {
            return org.openqa.selenium.By.xpath(myFindBy.xpath());
        }

        if (isNotEmpty(myFindBy.sclocator())) {
            return By.sclocator(myFindBy.sclocator());
        }
        if (isNotEmpty(myFindBy.jquery())) {
            return By.jquery(myFindBy.jquery());
        }
        if (isNotEmpty(myFindBy.accessibilityId())) {
            return AppiumBy.accessibilityId(myFindBy.accessibilityId());
        }
        if (isNotEmpty(myFindBy.androidUIAutomator())) {
            return AppiumBy.androidUIAutomator(myFindBy.androidUIAutomator());
        }
//        if (isNotEmpty(myFindBy.iOSUIAutomation())) {
//            // FIXME: Need to support android with platform switch
//            return MobileBy.IosUIAutomation(myFindBy.iOSUIAutomation());
//        }
        // Fall through
        return null;
    }

    private org.openqa.selenium.By getMobileBy(Annotation annotation, String valueName) {
        Strategies strategies[] = Strategies.values();
        for (Strategies strategy : strategies) {
            if (strategy.returnValueName().equals(valueName)) {
                return strategy.getBy(annotation);
            }
        }
        throw new IllegalArgumentException("@"
                + annotation.getClass().getSimpleName()
                + ": There is an unknown strategy " + valueName);
    }

    @SuppressWarnings("unchecked")
    private <T extends org.openqa.selenium.By> T getComplexMobileBy(Annotation[] annotations, Class<T> requiredByClass) {
        ;
        org.openqa.selenium.By[] byArray = new org.openqa.selenium.By[annotations.length];
        for (int i = 0; i < annotations.length; i++) {
            byArray[i] = getMobileBy(annotations[i],
                    getFieldValue(annotations[i]));
        }
        try {
            Constructor<?> c = requiredByClass.getConstructor(org.openqa.selenium.By[].class);
            Object[] values = new Object[]{byArray};
            return (T) c.newInstance(values);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFieldValue(Annotation mobileBy) {
        Method[] values = prepareAnnotationMethods(mobileBy.getClass());
        for (Method value : values) {
            try {
                String strategyParameter = value.invoke(mobileBy).toString();
                if (isNotEmpty(strategyParameter) && isStrategyName(value.getName())) {
                    return value.getName();
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("@"
                + mobileBy.getClass().getSimpleName() + ": one of "
                + Arrays.toString(Strategies.strategyNames()) + " should be filled");
    }

    private static boolean isStrategyName(String potentialStrategyName) {
        return Arrays.asList(Strategies.strategyNames()).contains(potentialStrategyName);
    }

    private static Method[] prepareAnnotationMethods(
            Class<? extends Annotation> annotation) {
        List<String> targetAnnotationMethodNamesList = getMethodNames(annotation.getDeclaredMethods());
        targetAnnotationMethodNamesList.removeAll(METHODS_TO_BE_EXCLUDED_WHEN_ANNOTATION_IS_READ);
        Method[] result = new Method[targetAnnotationMethodNamesList.size()];
        for (String methodName : targetAnnotationMethodNamesList) {
            try {
                result[targetAnnotationMethodNamesList.indexOf(methodName)] = annotation.getMethod(methodName, DEFAULT_ANNOTATION_METHOD_ARGUMENTS);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private static List<String> getMethodNames(Method[] methods) {
        List<String> names = new ArrayList<String>();
        for (Method m : methods) {
            names.add(m.getName());
        }
        return names;
    }

    private final static List<String> METHODS_TO_BE_EXCLUDED_WHEN_ANNOTATION_IS_READ = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;

        {
            List<String> objectClassMethodNames = getMethodNames(Object.class.getDeclaredMethods());
            addAll(objectClassMethodNames);
            List<String> annotationClassMethodNames = getMethodNames(Annotation.class.getDeclaredMethods());
            annotationClassMethodNames.removeAll(objectClassMethodNames);
            addAll(annotationClassMethodNames);
            add("proxyClassLookup");
        }
    };

    private void assertValidFindBy(FindBy findBy) {
        if (findBy.how() != null) {
            if (findBy.using() == null) {
                throw new IllegalArgumentException(
                        "If you set the 'how' property, you must also set 'using'");
            }
        }

        Set<String> finders = new HashSet<String>();
        if (!"".equals(findBy.using())) finders.add("how: " + findBy.using());
        if (!"".equals(findBy.className())) finders.add("class name:" + findBy.className());
        if (!"".equals(findBy.css())) finders.add("css:" + findBy.css());
        if (!"".equals(findBy.id())) finders.add("id: " + findBy.id());
        if (!"".equals(findBy.linkText())) finders.add("link text: " + findBy.linkText());
        if (!"".equals(findBy.name())) finders.add("name: " + findBy.name());
        if (!"".equals(findBy.ngModel())) finders.add("ngModel: " + findBy.ngModel());
        if (!"".equals(findBy.partialLinkText())) finders.add("partial link text: " + findBy.partialLinkText());
        if (!"".equals(findBy.tagName())) finders.add("tag name: " + findBy.tagName());
        if (!"".equals(findBy.xpath())) finders.add("xpath: " + findBy.xpath());
        if (!"".equals(findBy.sclocator())) finders.add("scLocator: " + findBy.sclocator());
        if (!"".equals(findBy.jquery())) finders.add("jquery: " + findBy.jquery());
        if (!"".equals(findBy.accessibilityId())) finders.add("accessibilityId: " + findBy.accessibilityId());
        if (!"".equals(findBy.androidUIAutomator())) finders.add("androidUIAutomator: " + findBy.androidUIAutomator());
        if (!"".equals(findBy.iOSUIAutomation())) finders.add("iOSUIAutomation: " + findBy.iOSUIAutomation());

        // A zero count is okay: it means to look by name or id.
        if (finders.size() > 1) {
            throw new IllegalArgumentException(
                    String.format("You must specify at most one location strategy. Number found: %d (%s)",
                            finders.size(), finders.toString()));
        }
    }

    // START - [dep3] *** todo [deprecate thucydides] Remove this once thucydides package name is removed

    /**
     * @deprecated use serenitybdd variation
     */
    @Deprecated
    protected org.openqa.selenium.By buildByFromFindBy(net.thucydides.core.annotations.findby.FindBy myDepFindBy) {
        assertValidFindBy(myDepFindBy);

        org.openqa.selenium.By ans = buildByFromShortFindBy(myDepFindBy);
        if (ans == null) {
            ans = buildByFromLongFindBy(myDepFindBy);
        }

        return ans;
    }

    /**
     * @deprecated use serenitybdd variation
     */
    @Deprecated
    protected org.openqa.selenium.By buildByFromLongFindBy(net.thucydides.core.annotations.findby.FindBy myDepFindBy) {
        How how = myDepFindBy.how();
        String using = myDepFindBy.using();

        switch (how) {
            case CLASS_NAME:
                return org.openqa.selenium.By.className(using);

            case CSS:
                return org.openqa.selenium.By.cssSelector(using);

            case ID:
                return org.openqa.selenium.By.id(using);

            case ID_OR_NAME:
                return new ByIdOrName(using);

            case LINK_TEXT:
                return org.openqa.selenium.By.linkText(using);

            case NAME:
                return org.openqa.selenium.By.name(using);

            case PARTIAL_LINK_TEXT:
                return org.openqa.selenium.By.partialLinkText(using);

            case TAG_NAME:
                return org.openqa.selenium.By.tagName(using);

            case XPATH:
                return org.openqa.selenium.By.xpath(using);

            case JQUERY:
                return By.jquery(using);

            case SCLOCATOR:
                return By.sclocator(using);

            default:
                // Note that this shouldn't happen (eg, the above matches all
                // possible values for the How enum)
                throw new IllegalArgumentException("Cannot determine how to locate element " + field);
        }
    }

    /**
     * @deprecated use serenitybdd variation
     */
    @Deprecated
    protected org.openqa.selenium.By buildByFromShortFindBy(net.thucydides.core.annotations.findby.FindBy myDepFindBy) {
        if (isNotEmpty(myDepFindBy.className())) {
            return org.openqa.selenium.By.className(myDepFindBy.className());
        }
        if (isNotEmpty(myDepFindBy.css())) {
            return org.openqa.selenium.By.cssSelector(myDepFindBy.css());
        }
        if (isNotEmpty(myDepFindBy.id())) {
            return org.openqa.selenium.By.id(myDepFindBy.id());
        }
        if (isNotEmpty(myDepFindBy.linkText())) {
            return org.openqa.selenium.By.linkText(myDepFindBy.linkText());
        }
        if (isNotEmpty(myDepFindBy.name())) {
            return org.openqa.selenium.By.name(myDepFindBy.name());
        }
        if (isNotEmpty(myDepFindBy.ngModel())) {
            return org.openqa.selenium.By.cssSelector("*[ng-model='" + myDepFindBy.ngModel() + "']");
        }

        if (isNotEmpty(myDepFindBy.partialLinkText())) {
            return org.openqa.selenium.By.partialLinkText(myDepFindBy.partialLinkText());
        }
        if (isNotEmpty(myDepFindBy.tagName())) {
            return org.openqa.selenium.By.tagName(myDepFindBy.tagName());
        }

        if (isNotEmpty(myDepFindBy.xpath())) {
            return org.openqa.selenium.By.xpath(myDepFindBy.xpath());
        }

        if (isNotEmpty(myDepFindBy.sclocator())) {
            return By.sclocator(myDepFindBy.sclocator());
        }
        if (isNotEmpty(myDepFindBy.jquery())) {
            return By.jquery(myDepFindBy.jquery());
        }
        // Fall through
        return null;
    }

    /**
     * @deprecated use serenitybdd variation
     */
    @Deprecated
    private void assertValidFindBy(net.thucydides.core.annotations.findby.FindBy findDepBy) {
        if (findDepBy.how() != null) {
            if (findDepBy.using() == null) {
                throw new IllegalArgumentException(
                        "If you set the 'how' property, you must also set 'using'");
            }
        }

        Set<String> finders = new HashSet<String>();
        if (!"".equals(findDepBy.using())) finders.add("how: " + findDepBy.using());
        if (!"".equals(findDepBy.className())) finders.add("class name:" + findDepBy.className());
        if (!"".equals(findDepBy.css())) finders.add("css:" + findDepBy.css());
        if (!"".equals(findDepBy.id())) finders.add("id: " + findDepBy.id());
        if (!"".equals(findDepBy.linkText())) finders.add("link text: " + findDepBy.linkText());
        if (!"".equals(findDepBy.name())) finders.add("name: " + findDepBy.name());
        if (!"".equals(findDepBy.ngModel())) finders.add("ngModel: " + findDepBy.ngModel());
        if (!"".equals(findDepBy.partialLinkText())) finders.add("partial link text: " + findDepBy.partialLinkText());
        if (!"".equals(findDepBy.tagName())) finders.add("tag name: " + findDepBy.tagName());
        if (!"".equals(findDepBy.xpath())) finders.add("xpath: " + findDepBy.xpath());
        if (!"".equals(findDepBy.sclocator())) finders.add("scLocator: " + findDepBy.sclocator());
        if (!"".equals(findDepBy.jquery())) finders.add("jquery: " + findDepBy.jquery());

        // A zero count is okay: it means to look by name or id.
        if (finders.size() > 1) {
            throw new IllegalArgumentException(
                    String.format("You must specify at most one location strategy. Number found: %d (%s)",
                            finders.size(), finders.toString()));
        }
    }
    // END - [dep3] ***[deprecate thucydides] Remove this once thucydides package name is removed
}
