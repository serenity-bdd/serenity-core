package net.thucydides.model.requirements.reports;

import net.thucydides.model.domain.TestResult;

interface RequirmentCalculator {
        long countAllSubrequirements();
        long countSubrequirementsWithResult(TestResult result);
        long countSubrequirementsWithNoTests();
    }
