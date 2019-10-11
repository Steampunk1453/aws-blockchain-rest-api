package com.innocv.hyperledger.config;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.util.Set;

public class UserContext implements User {
    @Override
    public String getName() {
        return "";
    }

    @Override
    public Set<String> getRoles() {
        return null;
    }

    @Override
    public String getAccount() {
        return null;
    }

    @Override
    public String getAffiliation() {
        return "";
    }

    @Override
    public Enrollment getEnrollment() {
        return null;
    }

    @Override
    public String getMspId() {
        return null;
    }
}
