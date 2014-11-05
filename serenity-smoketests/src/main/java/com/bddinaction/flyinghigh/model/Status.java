package com.bddinaction.flyinghigh.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Status {

    Bronze(0), Silver(300), Gold(700), Platinum(1500);

    private final int minimumPoints;

    Status(int minimumPoints) {
        this.minimumPoints = minimumPoints;
    }

    public static Status statusLevelFor(int points) {
        List<Status> statusesInDescendingOrder = Arrays.asList(Status.values());
        Collections.reverse(statusesInDescendingOrder);
        for(Status status : statusesInDescendingOrder) {
            if (points >= status.getMinimumPoints()) {
                return status;
            }
        }
        return Bronze;
    }

    public int getMinimumPoints() {
        return minimumPoints;
    }


}
