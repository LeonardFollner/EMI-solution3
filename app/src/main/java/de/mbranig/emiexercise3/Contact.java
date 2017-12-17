package de.mbranig.emiexercise3;

import java.io.Serializable;

/**
 * Created by meinhardtbranig on 15.11.17.
 */

public class Contact implements Serializable {
    private String title, firstname, lastname, address, zip, city, country;

    public Contact(String title, String firstname, String lastname, String address, String zip, String city, String country) {
        this.title = title;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.zip = zip;
        this.city = city;
        this.country = country;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
