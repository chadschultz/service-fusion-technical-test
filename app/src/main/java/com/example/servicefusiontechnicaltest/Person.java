package com.example.servicefusiontechnicaltest;

import java.io.Serializable;
import java.util.Date;

/**
 * Object to represent people.
 *
 * Created by Chad Schultz on 1/30/2016.
 */
public class Person implements Serializable {
    private static final long serialVersionUID = 7832243914440535763L;

    private String mId; // Firebase key, internal use only
    private String mFirstName;
    private String mLastName;
    private String mBirthDate;
    private String mZipCode;

    public Person() {
    }

    public Person(String firstName, String lastName, String birthDate, String zipCode) {
        mFirstName = firstName;
        mLastName = lastName;
        mBirthDate = birthDate;
        mZipCode = zipCode;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getBirthDate() {
        return mBirthDate;
    }

    public void setBirthDate(String birthDate) {
        mBirthDate = birthDate;
    }

    public String getZipCode() {
        return mZipCode;
    }

    public void setZipCode(String zipCode) {
        mZipCode = zipCode;
    }
}
