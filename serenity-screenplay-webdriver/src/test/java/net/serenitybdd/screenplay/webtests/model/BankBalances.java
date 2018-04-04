package net.serenitybdd.screenplay.webtests.model;

import com.google.common.base.MoreObjects;

public class BankBalances {

    private String currentAccount;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("currentAccount", currentAccount)
                .add("savingsAccount", savingsAccount)
                .toString();
    }

    private String savingsAccount;

    public BankBalances(String currentAccount, String savingsAccount) {
        this.currentAccount = currentAccount;
        this.savingsAccount = savingsAccount;
    }

    public String getCurrentAccount() {
        return currentAccount;
    }

    public String getSavingsAccount() {
        return savingsAccount;
    }
}
