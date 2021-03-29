package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.questions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.Text;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.model.BankBalances;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.pages.BankAccountBalanceTargets;

public class BankBalanceQuestion implements Question<BankBalances> {
    @Override
    public BankBalances answeredBy(Actor actor) {
        String currentAccount = Text.of(BankAccountBalanceTargets.CURRENT_ACCOUNT_BALANCE)
                .viewedBy(actor)
                .value();

        String savingsAccount = Text.of(BankAccountBalanceTargets.SAVINGS_ACCOUNT_BALANCE)
                .viewedBy(actor)
                .value();

        return new BankBalances(currentAccount, savingsAccount);
    }
}
