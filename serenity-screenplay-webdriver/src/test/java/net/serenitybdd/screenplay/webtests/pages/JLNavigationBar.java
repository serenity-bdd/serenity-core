package net.serenitybdd.screenplay.webtests.pages;

import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.webtests.model.Category;
import net.serenitybdd.screenplay.webtests.model.SubCategory;
import org.openqa.selenium.By;

public class JLNavigationBar {
    public static Target category(Category category)
    {
        return Target.the(category.name() + " category")
                .locatedBy("//li[@class=\"jl-has-submenu\"]//span[contains(text(),'{0}')]")
                .of(category.name());
        /*return Target.the(category.name() + " category")
                .located(By.cssSelector("#{0}Menu"));*/
    }
    public static Target subCategory(SubCategory subCategory)
    {
        String subCategoryName = subCategory.getName();
        return Target.the(subCategory + " sub-category")
                .locatedBy("//ul[@class='jl-submenu menu-open']//a[contains(text(),'#subCategoryName')]")
                .of(subCategory.name());
    }
}
