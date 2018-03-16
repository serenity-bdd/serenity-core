package net.serenitybdd.screenplay.webtests.pages;

import net.serenitybdd.screenplay.targets.IFrame;
import net.serenitybdd.screenplay.targets.Target;

public class BankAccountBalanceTargets {

    private static IFrame PROFILE_BANK_ACCOUNT_BALANCES = IFrame.withPath("profile-bank", "profile-bank-balance");
    public static final Target CURRENT_ACCOUNT_BALANCE = Target.the("current account balance").inIFrame(PROFILE_BANK_ACCOUNT_BALANCES).locatedBy("#currentAccountBalance");
    public static final Target SAVINGS_ACCOUNT_BALANCE = Target.the("savings account balance").inIFrame(PROFILE_BANK_ACCOUNT_BALANCES).locatedBy("#savingsAccountBalance");
}
