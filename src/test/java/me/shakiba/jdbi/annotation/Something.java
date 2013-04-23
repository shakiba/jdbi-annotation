package me.shakiba.jdbi.annotation;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Something {
    @Column
    private int id;
    @Column
    private String name;

    @SuppressWarnings("unused")
    private Something() {
    }

    public Something(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int id() {
        return id;
    }

    public void id(int id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }
}