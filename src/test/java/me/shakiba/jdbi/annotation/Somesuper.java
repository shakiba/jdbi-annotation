package me.shakiba.jdbi.annotation;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Somesuper {
    @Column
    private int id;

    Somesuper() {
    }

    Somesuper(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public void id(int id) {
        this.id = id;
    }
}
