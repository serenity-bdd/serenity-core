package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenityscreenplay.net.serenitybdd.screenplay.Task;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.Enter;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.model.BankAccount;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.pages.BankAccountEntry;
import serenitymodel.net.thucydides.core.annotations.Step;

import static serenityscreenplay.net.serenitybdd.screenplay.Tasks.instrumented;

public class EnterBankDetails implements Task {

    private final BankAccount bankAccount;

    @Override
    @Step("Enter bank account details for #bankAccount")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Enter.theValue(bankAccount.getName()).into(BankAccountEntry.ACCOUNT_NAME),
                Enter.theValue(bankAccount.getBsb()).into(BankAccountEntry.BSB),
                Enter.theValue(bankAccount.getAccountNumber()).into(BankAccountEntry.ACCOUNT_NUMBER)
        );
    }

    public EnterBankDetails(BankAccount bankAccount)
    {
        this.bankAccount = bankAccount;
    }

    public static Performable forAccount(BankAccount bankAccount) {
        return instrumented(EnterBankDetails.class, bankAccount);
    }

}