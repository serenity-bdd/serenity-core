package net.serenitybdd.screenplay.webtests.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;

public class JLSitesAddSitePage extends PageObject {
    //Job Logic - Site Page Selectors
    private static final String CUSTOMER = "input[name='CustomerId_input']";
    private static final String SITE = "#SiteName";
    private static final String TAGS = "input[name='CustomerId_input']";
    private static final String ADDSTREET = "#SiteAddress1";
    private static final String ADDAREA = "#SiteAddress2";
    private static final String ADDCITY = "SiteAddress3";
    private static final String ADDCOUNTY = "SiteAddress4";
    private static final String POSTCODE = "input[name='CustomerId_input']";
    private static final String TELEPHONE = "input[name='CustomerId_input']";
    private static final String AREA = "input[name='CustomerId_input']";
    private static final String SITEREFNO = "input[name='CustomerId_input']";
    private static final String CNTFIRSTNAME = "input[name='CustomerId_input']";
    private static final String CNTLASTNAME = "input[name='CustomerId_input']";
    private static final String CNTTELEPHONE = "input[name='CustomerId_input']";
    private static final String CNTEMAIL = "input[name='CustomerId_input']";
    private static final String CNTJOBPOSITION = "input[name='CustomerId_input']";
    private static final String CANCEL = "input[name='CustomerId_input']";
    private static final String SAVE = "input[name='CustomerId_input']";

    public static Target DDCUSTOMER = Target.the("CustomerName").locatedBy(CUSTOMER);
    public static Target TXTSITE = Target.the("SiteName").locatedBy(SITE);
    public static Target DDTAGS = Target.the("Tags").locatedBy(TAGS);
    public static Target TXTADDSTREET = Target.the("AddressStreet").locatedBy(ADDSTREET);
    public static Target TXTADDAREA = Target.the("AddressArea").locatedBy(ADDAREA);
    public static Target TXTADDCITY = Target.the("AddressCity").locatedBy(ADDCITY);
    public static Target TXTADDCOUNTY = Target.the("AddressCounty").locatedBy(ADDCOUNTY);
    public static Target TXTPOSTCODE = Target.the("PostCode").locatedBy(POSTCODE);
    public static Target TXTTEL = Target.the("Telephone").locatedBy(TELEPHONE);
    public static Target DDAREA = Target.the("Area").locatedBy(AREA);
    public static Target TXTREFNO = Target.the("SiteReferenceNumber").locatedBy(SITEREFNO);
    public static Target TXTCNTFIRSTNAME = Target.the("ContactFirstName").locatedBy(CNTFIRSTNAME);
    public static Target TXTCNTLASTNAME = Target.the("ContactLastName").locatedBy(CNTLASTNAME);
    public static Target TXTCNTTEL = Target.the("ContactTelephone").locatedBy(CNTTELEPHONE);
    public static Target TXTCNTEMAIL = Target.the("ContactEmail").locatedBy(CNTEMAIL);
    public static Target TXTCNTJOBPOS= Target.the("ContactJobPosition").locatedBy(CNTJOBPOSITION);
    public static Target BTNCANCEL = Target.the("Cancel").locatedBy(CANCEL);
    public static Target BTNSAVE = Target.the("Save").locatedBy(SAVE);

   /* public void enterSiteInfo(String customerName, String site, String tags[], String addStreet
            , String addArea, String addCity, String addCounty, String postcode, String tel, String Area
            , String refNo, String firstName, String lastname, String cntTel, String cntEmail, String cntJobPos
            , String btnToClick) {
        $(CUSTOMER).selectByValue(customerName);
        $(SITE).type(site);
        //for (String tagItem : tags) { $(TAGS).selectByValue(tagItem);}
        $(ADDSTREET).type(addStreet);
        $(ADDAREA).type(addArea);
        $(ADDCITY).type(addCity);
        $(ADDCOUNTY).type(addCounty);
        $(POSTCODE).type(postcode);
    }*/

    @Step("{0} views her profile")
    public <T extends Actor> void performAs(T actor) {
        //actor.attemptsTo(enterSiteInfo(customer, site));
    }
    public void enterSiteInfo(String customerName, String site) {
        $(CUSTOMER).selectByValue(customerName);
        $(SITE).type(site);
        // for (String tagItem : tags) { $(TAGS).selectByValue(tagItem);}
        // $(ADDSTREET).type(addStreet);
        // $(ADDAREA).type(addArea);
        // $(ADDCITY).type(addCity);
        // $(ADDCOUNTY).type(addCounty);
        //  $(POSTCODE).type(postcode);
    }
}
