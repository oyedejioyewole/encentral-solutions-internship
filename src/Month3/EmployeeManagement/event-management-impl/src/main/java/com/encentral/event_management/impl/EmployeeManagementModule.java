package com.encentral.event_management.impl;

import com.encentral.event_management.api.IEmployeeManagement;
import com.google.inject.AbstractModule;

public class EmployeeManagementModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IEmployeeManagement.class).to(DefaultEmployeeManagementImpl.class);
    }
}