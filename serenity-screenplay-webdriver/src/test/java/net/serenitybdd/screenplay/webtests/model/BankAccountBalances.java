package net.serenitybdd.screenplay.webtests.model;

import com.google.common.base.Objects;

public class BankAccountBalances
{
    private String name;

    private String bsb;

    private String accountNumber;

    public BankAccountBalances(String name, String bsb, String accountNumber )
    {
        this.name = name;
        this.bsb = bsb;
        this.accountNumber = accountNumber;
    }

    public String getName()
    {
        return name;
    }

    public BankAccountBalances setName(String name )
    {
        this.name = name;
        return this;
    }

    public String getBsb()
    {
        return bsb;
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }


    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        BankAccountBalances that = (BankAccountBalances) o;

        if ( name != null && that.name != null )
        {
            if ( name != null ? !name.equals( that.name ) : that.name != null )
            {
                return false;
            }
        }
        if ( accountNumber != null && that.accountNumber != null )
        {
            if ( accountNumber != null ? !accountNumber.equals( that.accountNumber ) : that.accountNumber != null )
            {
                return false;
            }
        }
        if ( bsb != null && that.bsb != null )
        {
            if ( bsb != null ? !bsb.equals( that.bsb ) : that.bsb != null )
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( accountNumber != null ? accountNumber.hashCode() : 0 );
        result = 31 * result + ( bsb != null ? bsb.hashCode() : 0 );
        return result;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this )
                .add( "accountName", name )
                .add( "accountNumber", accountNumber )
                .add( "bsb", bsb )
                .toString();
    }

    public static BankAccountBalances forAStandardAccount()
    {
        return aStandardBankAccount();
    }

    public BankAccountBalances withName(String newName )
    {
        return new BankAccountBalances( newName, bsb, accountNumber );
    }

    public BankAccountBalances withABsb(String bsb )
    {
        this.bsb = bsb;
        return this;
    }

    public BankAccountBalances withAccountNumber(String accountNumber )
    {
        this.accountNumber = accountNumber;
        return this;
    }

    public BankAccountBalances withAccountName(String accountName )
    {
        this.name = accountName;
        return this;
    }

    public static BankAccountBalances aStandardBankAccount()
    {
        return new BankAccountBalances(
                "Joe Blogs",
                "123456",
                "012345678");
    }

    public static BankAccountBalances aBankAccount()
    {
        return new BankAccountBalances(null, null, null);
    }

    public boolean matches(BankAccountBalances that) {

        if ( name != null )
        {
            if ( name != null ? !name.equals( that.name ) : that.name != null )
            {
                return false;
            }
        }
        if ( accountNumber != null )
        {
            if ( accountNumber != null ? !accountNumber.equals( that.accountNumber ) : that.accountNumber != null )
            {
                return false;
            }
        }
        if ( bsb != null )
        {
            if ( bsb != null ? !bsb.equals( that.bsb ) : that.bsb != null )
            {
                return false;
            }
        }
        return true;
    }
}
