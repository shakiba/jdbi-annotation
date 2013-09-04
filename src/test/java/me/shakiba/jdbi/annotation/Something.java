package me.shakiba.jdbi.annotation;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Something extends Somesuper {
    @Column
    private String name;

    @Column
    public Long value;

    @SuppressWarnings("unused")
    private Something() {
    }

    public Something(int id, String name, Long value) {
        super(id);
        this.name = name;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }
}