package net.serenitybdd.screenplay.webtests.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.webtests.model.BankBalances;
import net.serenitybdd.screenplay.webtests.pages.BankAccountBalanceTargets;

public class BankBalanceQuestion implements Question<BankBalances> {
    @Override
    public BankBalances answeredBy(Actor actor) {
        String currentAccount = Text.of(BankAccountBalanceTargets.CURRENT_ACCOUNT_BALANCE).answeredBy(actor);
        String savingsAccount = Text.of(BankAccountBalanceTargets.SAVINGS_ACCOUNT_BALANCE).answeredBy(actor);

        return new BankBalances(currentAccount, savingsAccount);
    }
}
