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

        public void startQuestion(Actor actor) {
            if (shouldReportConsequence()) {
                eventBusInterface.startQuestion(FormattedTitle.ofConsequence(consequence, actor));
            }
        }

        private boolean shouldReportConsequence() {
            if ((consequence instanceof CanBeSilent) && (((CanBeSilent) consequence).isSilent())) return false;
            if (SilentTasks.isNestedInSilentTask()) return false;
            return true;
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