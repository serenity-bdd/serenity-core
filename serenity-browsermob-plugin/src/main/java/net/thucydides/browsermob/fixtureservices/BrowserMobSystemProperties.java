package net.thucydides.browsermob.fixtureservices;


public enum BrowserMobSystemProperties {

    /**
     * If the browser mob configuration need only be applied for certain drivers, list them in this variable.
     */
    BROWSER_MOB_FILTER,

    /**
     * Optional proxy port to use for Browser Mob.
     */
    BROWSER_MOB_PROXY;

    @Override
    public String toString() {
        {return super.toString().toLowerCase().replaceAll("_",".");}
    }
}
