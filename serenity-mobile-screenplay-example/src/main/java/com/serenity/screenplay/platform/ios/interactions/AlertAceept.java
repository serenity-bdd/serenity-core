package com.serenity.screenplay.platform.ios.interactions;

import com.serenity.screenplay.platform.ios.IOSObject;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;

public class AlertAceept extends IOSObject implements Interaction {

	@Override
	public <T extends Actor> void performAs(T t) {
		getDriver(t).switchTo().alert().accept();
	}

}
