package org.inanme.spring;

import com.google.common.base.MoreObjects;

public class WebClient {

    private String name;

    private String surname;

    public WebClient(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public WebClient() {
    }

    public static WebClient create(String name) {
        WebClient wc = new WebClient();
        wc.setName(name);
        return wc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WebClient setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", name).add("surname", surname).toString();
    }
}
