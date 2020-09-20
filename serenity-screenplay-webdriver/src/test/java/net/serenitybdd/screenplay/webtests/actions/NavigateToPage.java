package net.serenitybdd.screenplay.webtests.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.webtests.model.Category;
import net.serenitybdd.screenplay.webtests.model.SubCategory;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.webtests.pages.JLNavigationBar;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;


import java.time.Duration;

import static net.serenitybdd.screenplay.Tasks.instrumented;


public class NavigateToPage extends PageObject implements Task {

    private final Category menu;
    SubCategory item;

    public NavigateToPage(Category menu){
        this.menu = menu;
    }

    public static NavigateToPage withMenu(Category menu){
        return instrumented(NavigateToPage.class, menu);
    }

    public NavigateToPage selectItem(SubCategory item){
        this.item = item;
        return this;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        waitABit(10000);
        waitForCondition().withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofSeconds(3)).until(ExpectedConditions.elementToBeClickable(By.cssSelector("#siteMenu"))).click();
       // actor.attemptsTo(
         //       Click.on(JLNavigationBar.category(menu))
                //Click.on(Target.the("Site Name").located(By.xpath("//li[@class=\"jl-has-submenu\"]//span[contains(text(),'Sites')]")))
       // );
        waitForCondition().withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofSeconds(3)).until(ExpectedConditions.elementToBeClickable(By.cssSelector("#createSiteMenu"))).click();
      /*  if(item != null){
            actor.attemptsTo(
                   // WaitUntil.the(JLNavigationBar.subCategory(SubCategory.ADDSITE.getName()),isVisible()),
                    Click.on(JLNavigationBar.subCategory(item))
            );

        }*/
    }


}
