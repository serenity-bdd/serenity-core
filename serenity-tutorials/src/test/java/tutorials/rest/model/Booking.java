package tutorials.rest.model;

/**
 * Model class for Restful-Booker bookings.
 * https://restful-booker.herokuapp.com
 */
public class Booking {
    private String firstname;
    private String lastname;
    private Integer totalprice;
    private Boolean depositpaid;
    private BookingDates bookingdates;
    private String additionalneeds;

    public Booking() {}

    public Booking(String firstname, String lastname, Integer totalprice,
                   Boolean depositpaid, String checkin, String checkout,
                   String additionalneeds) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.totalprice = totalprice;
        this.depositpaid = depositpaid;
        this.bookingdates = new BookingDates(checkin, checkout);
        this.additionalneeds = additionalneeds;
    }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public Integer getTotalprice() { return totalprice; }
    public void setTotalprice(Integer totalprice) { this.totalprice = totalprice; }

    public Boolean getDepositpaid() { return depositpaid; }
    public void setDepositpaid(Boolean depositpaid) { this.depositpaid = depositpaid; }

    public BookingDates getBookingdates() { return bookingdates; }
    public void setBookingdates(BookingDates bookingdates) { this.bookingdates = bookingdates; }

    public String getAdditionalneeds() { return additionalneeds; }
    public void setAdditionalneeds(String additionalneeds) { this.additionalneeds = additionalneeds; }

    public static class BookingDates {
        private String checkin;
        private String checkout;

        public BookingDates() {}

        public BookingDates(String checkin, String checkout) {
            this.checkin = checkin;
            this.checkout = checkout;
        }

        public String getCheckin() { return checkin; }
        public void setCheckin(String checkin) { this.checkin = checkin; }

        public String getCheckout() { return checkout; }
        public void setCheckout(String checkout) { this.checkout = checkout; }
    }
}
