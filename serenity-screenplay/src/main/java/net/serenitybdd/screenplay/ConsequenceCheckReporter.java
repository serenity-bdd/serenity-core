package net.serenitybdd.screenplay;

import net.serenitybdd.markers.CanBeSilent;
import net.serenitybdd.screenplay.formatting.FormattedTitle;

class ConsequenceCheckReporter {
        private final EventBusInterface eventBusInterface;
        private final Consequence consequence;

        ConsequenceCheckReporter(EventBusInterface eventBusInterface, Consequence consequence) {
            this.eventBusInterface = eventBusInterface;
            this.consequence = consequence;
        }

        public void startQuestion() {
            if (shouldReportConsequence()) {
                eventBusInterface.startQuestion(FormattedTitle.ofConsequence(consequence));
            }
        }

        private boolean shouldReportConsequence() {
            return !((consequence instanceof CanBeSilent) && (((CanBeSilent) consequence).isSilent()));
        }


        public void reportStepIgnored() {
            if (shouldReportConsequence()) {
                eventBusInterface.reportStepIgnored();
            }

        }

        public void reportStepFinished() {
            if (shouldReportConsequence()) {
                eventBusInterface.reportStepFinished();
            }
        }
    }