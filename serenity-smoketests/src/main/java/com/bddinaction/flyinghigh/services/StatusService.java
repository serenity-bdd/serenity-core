package com.bddinaction.flyinghigh.services;

import com.bddinaction.flyinghigh.model.Status;

public interface StatusService {
    Status statusLevelFor(int statusPoints);
}
