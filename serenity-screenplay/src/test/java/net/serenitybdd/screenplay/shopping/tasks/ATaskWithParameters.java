package net.serenitybdd.screenplay.shopping.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;

import java.math.BigDecimal;

public class ATaskWithParameters implements Performable {
        public final int aPrimitiveType;
        public final String anObject;
        public final Number aParent;

        public ATaskWithParameters(int aPrimitiveType, String anObject, BigDecimal aParent) {
            this.aPrimitiveType = aPrimitiveType;
            this.anObject = anObject;
            this.aParent = aParent;
        }

        @Override
        public <T extends Actor> void performAs(T actor) {

        }
    }