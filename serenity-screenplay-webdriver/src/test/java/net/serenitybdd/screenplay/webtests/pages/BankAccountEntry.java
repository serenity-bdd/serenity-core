package net.serenitybdd.screenplay.webtests.pages;

import net.serenitybdd.screenplay.targets.Target;

public class BankAccountEntry {
    public static final Target ACCOUNT_NAME = Target.the("account name").locatedBy("#accountName");

    public static final Target BSB = Target.the("account bsb").locatedBy("#bsb");

    public static final Target ACCOUNT_NUMBER = Target.the("account number").locatedBy("#accountNumber");
}
