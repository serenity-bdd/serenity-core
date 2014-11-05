package com.bddinaction.flyinghigh.services;

import com.bddinaction.flyinghigh.model.Status;

public class InMemoryStatusService implements StatusService {
    public Status statusLevelFor(int statusPoints) {
        Status highestMatchingStatus = Status.Bronze;
        for(Status status : Status.values())  {
            if (statusPoints >= status.getMinimumPoints()) {
                highestMatchingStatus = status;
            }
        }
        return highestMatchingStatus;
    }
}
