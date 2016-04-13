package org.inanme.springdata.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CustomPojo {

    @Id
    public final int id;

    @Column(length = 1)
    public final String text;

    public CustomPojo(int id) {
        this.id = id;
        this.text = "";
    }

    public CustomPojo(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public CustomPojo() {
        this.id = 0;
        this.text = "";
    }

    @Override
    public String toString() {
        return "CustomPojo{id=" + id + '}';
    }
}
