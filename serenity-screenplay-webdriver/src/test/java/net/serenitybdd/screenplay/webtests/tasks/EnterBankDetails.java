package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.webtests.model.BankAccount;
import net.serenitybdd.screenplay.webtests.pages.BankAccountEntry;

import static net.serenitybdd.screenplay.Tasks.instrumented;

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
