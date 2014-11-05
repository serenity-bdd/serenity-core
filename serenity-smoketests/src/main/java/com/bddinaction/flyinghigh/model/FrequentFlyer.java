package com.bddinaction.flyinghigh.model;


import com.bddinaction.flyinghigh.services.InMemoryStatusService;
import com.bddinaction.flyinghigh.services.StatusService;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class FrequentFlyer {

    private final String frequentFlyerNumber;
    private final String firstName;
    private final String lastName;
    private Status status = Status.Bronze;
    private int statusPoints;
    private StatusService statusService;

    protected FrequentFlyer(String frequentFlyerNumber, String firstName, String lastName,
                          Status status, int statusPoints, StatusService statusService) {
        this.frequentFlyerNumber = frequentFlyerNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.statusPoints = statusPoints;
        this.statusService = statusService;
    }

    protected FrequentFlyer(String frequentFlyerNumber, String firstName, String lastName, StatusService statusService) {
        this(frequentFlyerNumber, firstName, lastName, Status.Bronze, 0, statusService);
    }

    public FrequentFlyer withStatus(Status newStatus) {
        this.setStatus(newStatus);
        return this;
    }

    public String getFrequentFlyerNumber() {
        return frequentFlyerNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Status getStatus() {
        return status;
    }

    public FrequentFlyer withStatusPoints(int statusPoints) {
        this.setStatusPoints(statusPoints);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStatusPoints(int statusPoints) {
        this.statusPoints = statusPoints;
        updateStatusLevel();
    }

    private void updateStatusLevel() {
        setStatus(statusService.statusLevelFor(statusPoints));
    }

    public int getStatusPoints() { return statusPoints; }

    public List<Status> getUnachievedStatuses() {
        return ImmutableList.of(Status.Gold, Status.Platinum);
    }


    public static FrequentFlyerBuilder withFrequentFlyerNumber(String frequentFlyerNumber) {
        return new FrequentFlyerBuilder(frequentFlyerNumber);
    }

    public static class FrequentFlyerBuilder {

        private String frequentFlyerNumber;

        public FrequentFlyerBuilder(String frequentFlyerNumber) {
            this.frequentFlyerNumber = frequentFlyerNumber;
        }

        public FrequentFlyer named(String firstName, String lastName) {
            return new FrequentFlyer(frequentFlyerNumber, firstName, lastName, new InMemoryStatusService());
        }
    }

    public PointEarner earns(int points) {
        return new PointEarner(this, points);
    }

    private void addStatusPoints(int extraPoints) {
        setStatusPoints(getStatusPoints() + extraPoints);
    }

    public static class PointEarner {
        private final int points;
        private final FrequentFlyer frequentFlyer;

        public PointEarner(FrequentFlyer frequentFlyer, int points) {
            this.frequentFlyer = frequentFlyer;
            this.points = points;
        }

        public void statusPoints() {
            frequentFlyer.addStatusPoints(points);
        }
    }

}
