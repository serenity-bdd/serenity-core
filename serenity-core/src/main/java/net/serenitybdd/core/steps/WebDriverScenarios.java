package net.serenitybdd.core.steps;

import net.serenitybdd.core.pages.PageObject;

/**
 * A convenience class that can be used for very simple web tests, e.g.
 * <pre>
 *     <code>
 *@ExtendWith(SerenityJUnit5Extension.class)
 * class ShowProducts extends WebDriverScenarios {
 *
 *     @Nested
 *     class ShowProductsByCategory {
 *
 *         @Test
 *         void shouldListAllTheCategories() {
 *             openUrl("https://www.demoblaze.com/index.html");
 *             assertThat(findAll(".list-group-item").texts()).contains("Phones","Laptops","Monitors");
 *         }
 *     }
 * }
 *     </code>>
 * </pre>
 */
public class WebDriverScenarios extends PageObject {}
