package net.serenitybdd.screenplay.webtests.pages;

import net.serenitybdd.screenplay.targets.IFrame;
import net.serenitybdd.screenplay.targets.Target;

public class BankAccountEntry {
    private static IFrame PROFILE_BANK = IFrame.withPath("profile-bank");

    public static final Target ACCOUNT_NAME = Target.the("account name").inIFrame(PROFILE_BANK).locatedBy("#accountName");

    public static final Target BSB = Target.the("account bsb").inIFrame(PROFILE_BANK).locatedBy("#bsb");

    public static final Target ACCOUNT_NUMBER = Target.the("account number").inIFrame(PROFILE_BANK).locatedBy("#accountNumber");
}
