package net.thucydides.core.requirements.reports;

import net.thucydides.core.model.TestResult;

interface RequirmentCalculator {
        int countAllSubrequirements();
        int countSubrequirementsWithResult(TestResult result);
        int countSubrequirementsWithNoTests();
    }
