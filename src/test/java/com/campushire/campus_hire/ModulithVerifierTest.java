package com.campushire.campus_hire;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithVerifierTest {

    @Test
    void verifyModulithArchitecture() {
        ApplicationModules.of(CampusHireApplication.class).verify();
    }
}