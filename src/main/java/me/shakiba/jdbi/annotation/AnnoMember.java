package me.shakiba.jdbi.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.Column;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AnnoMember {

    private Column column;
    private String name;
    private AnnoType annoType;
    private Field field;
    private Method method;
    private Class<?> clazz;

    public AnnoMember(Class<?> clazz, Field member) {
        this.clazz = clazz;
        this.field = member;
        this.column = member.getAnnotation(Column.class);
        this.name = nameOf(member, column);
        try {
            this.annoType = AnnoType.of(member.getType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknow member type: " + clazz
                    + "." + name);
        }
    }

    public AnnoMember(Class<?> clazz, Method member) {
        this.clazz = clazz;
        this.method = member;
        this.column = member.getAnnotation(Column.class);
        this.name = nameOf(member, column);
        try {
            this.annoType = AnnoType.of(member.getParameterTypes()[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknow member type: " + clazz
                    + "." + name);
        }
    }

    public String getName() {
        return name;
    }

    public AnnoType getType() {
        return annoType;
    }

    public Object read(Object obj) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        if (method != null) {
            method.setAccessible(true);
            return method.invoke(obj);
        }
        if (field != null) {
            field.setAccessible(true);
            return field.get(obj);
        }
        // unreachable!
        throw new RuntimeException("Reached unreachable!");
    }

    public void write(Object obj, Object value)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        if (logger.isDebugEnabled()) {
            logger.debug("write" + clazz + "/" + name + "/" + annoType + "/"
                    + value);
        }
        if (method != null) {
            method.setAccessible(true);
            method.invoke(obj, value);
        }
        if (field != null) {
            field.setAccessible(true);
            field.set(obj, value);
        }
    }

    private String nameOf(Field member, Column column) {
        String name = column.name();
        if (name == null || name.length() == 0) {
            name = member.getName();
        }
        return name;
    }

    private String nameOf(Method member, Column column) {
        String name = column.name();
        if (name == null || name.length() == 0) {
            name = member.getName();
            // TODO: drop set/get/is
        }
        return name;
    }

    private static Logger logger = LoggerFactory.getLogger(AnnoMember.class);
}