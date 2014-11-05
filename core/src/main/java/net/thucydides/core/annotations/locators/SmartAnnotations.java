package net.thucydides.core.annotations.locators;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.annotations.findby.How;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.Annotations;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;


public class SmartAnnotations extends Annotations {

    private Field field;

    public SmartAnnotations(Field field) {
        super(field);
        this.field = field;
    }

    private void assertValidAnnotations() {
        FindBys findBys = field.getAnnotation(FindBys.class);
        FindBy myFindBy = field.getAnnotation(FindBy.class);
        if (findBys != null && myFindBy != null) {
            throw new IllegalArgumentException("If you use a '@FindBys' annotation, "
                    + "you must not also use a '@FindBy' annotation");
        }
    }

    //	@Override
    public org.openqa.selenium.By buildBy() {
        assertValidAnnotations();

        org.openqa.selenium.By ans = null;

        //default implementation in case if org.openqa.selenium.support.FindBy was used
        org.openqa.selenium.support.FindBy findBy = field.getAnnotation(org.openqa.selenium.support.FindBy.class);
        if (ans == null && findBy != null) {
            ans = super.buildByFromFindBy(findBy);
        }


        //my additions to FindBy
        FindBy myFindBy = field.getAnnotation(FindBy.class);
        if (ans == null && myFindBy != null) {
            ans = buildByFromFindBy(myFindBy);
        }


        FindBys findBys = field.getAnnotation(FindBys.class);
        if (ans == null && findBys != null) {
            ans = buildByFromFindBys(findBys);
        }


        if (ans == null) {
            ans = buildByFromDefault();
        }

        if (ans == null) {
            throw new IllegalArgumentException("Cannot determine how to locate element " + field);
        }

        return ans;
    }


    protected org.openqa.selenium.By buildByFromFindBy(FindBy myFindBy) {
        assertValidFindBy(myFindBy);

        org.openqa.selenium.By ans = buildByFromShortFindBy(myFindBy);
        if (ans == null) {
            ans = buildByFromLongFindBy(myFindBy);
        }

        return ans;
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
        // Fall through
        return null;
    }


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

        // A zero count is okay: it means to look by name or id.
        if (finders.size() > 1) {
            throw new IllegalArgumentException(
                    String.format("You must specify at most one location strategy. Number found: %d (%s)",
                            finders.size(), finders.toString()));
        }
    }
}