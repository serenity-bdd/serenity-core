package net.thucydides.core.requirements.reports;

import net.thucydides.core.model.TestResult;

interface RequirmentCalculator {
        long countAllSubrequirements();
        long countSubrequirementsWithResult(TestResult result);
        long countSubrequirementsWithNoTests();
    }
