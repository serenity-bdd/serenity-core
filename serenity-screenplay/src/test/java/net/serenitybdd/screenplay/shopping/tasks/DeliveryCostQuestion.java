
package net.serenitybdd.screenplay.shopping.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class DeliveryCostQuestion implements Question<Integer> {

	@Step("Then {0} expects the delivery cost to be correct")
	public Integer answeredBy(Actor actor) {
		 return 20;
	 }

}
