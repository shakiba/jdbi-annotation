package me.shakiba.jdbi.annotation;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Something extends Somesuper {
    @Column
    private String name;

    @SuppressWarnings("unused")
    private Something() {
    }

    public Something(int id, String name) {
        super(id);
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }
}