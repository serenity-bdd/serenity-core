package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.RecordsInputs;
import serenityscreenplay.net.serenitybdd.screenplay.Task;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.model.BankAccount;

public class EnterABankAccount implements Task, RecordsInputs
{
    private final BankAccount bankAccount;
    private final String value;

    public EnterABankAccount(BankAccount bankAccount, String value) {
        this.bankAccount = bankAccount;
        this.value = value;
    }

    public static EnterABankAccount bsbValueOf(String bsb) {
        return new EnterABankAccount(BankAccount.aStandardBankAccount().withABsb(bsb),bsb);
    }

    public static EnterABankAccount accountNumberOf(String accountNumber) {
        return new EnterABankAccount(BankAccount.aStandardBankAccount().withAccountNumber(accountNumber),accountNumber);
    }

    public static EnterABankAccount accountNameOf(String accountName) {
        return new EnterABankAccount(BankAccount.aStandardBankAccount().withAccountName(accountName),accountName);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo( EnterBankDetails.forAccount( bankAccount ) );
    }

    @Override
    public String getInputValues() {
        return value;
    }
}
