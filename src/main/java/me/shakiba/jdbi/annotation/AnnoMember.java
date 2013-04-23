package me.shakiba.jdbi.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Date;

import javax.persistence.Column;

import org.apache.log4j.Logger;

class AnnoMember {

    private Column column;
    private String name;
    private Type type;
    private Field field;
    private Method method;
    private Class<?> clazz;

    public AnnoMember(Class<?> clazz, Field member) {
        this.clazz = clazz;
        this.field = member;
        this.column = member.getAnnotation(Column.class);
        this.name = nameOf(member, column);
        this.type = typeOf(member.getType());
    }

    public AnnoMember(Class<?> clazz, Method member) {
        this.clazz = clazz;
        this.method = member;
        this.column = member.getAnnotation(Column.class);
        this.name = nameOf(member, column);
        this.type = typeOf(member.getParameterTypes()[0]);
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
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
            logger.debug("write" + clazz + "/" + name + "/" + type + "/"
                    + value);
        }
        if (value == null) {
            return;
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

    private String nameOf(Member member, Column column) {
        String name = column.name();
        if (name == null || name.length() == 0) {
            name = member.getName();
            // TODO: drop starting set/get
        }
        return name;
    }

    private static Type typeOf(Class<?> type) {
        if (String.class.isAssignableFrom(type)) {
            return Type.String;

        } else if (Long.class.isAssignableFrom(type)
                || long.class.isAssignableFrom(type)) {
            return Type.Long;

        } else if (Integer.class.isAssignableFrom(type)
                || int.class.isAssignableFrom(type)) {
            return Type.Int;

        } else if (Double.class.isAssignableFrom(type)
                || double.class.isAssignableFrom(type)) {
            return Type.Double;

        } else if (Float.class.isAssignableFrom(type)
                || float.class.isAssignableFrom(type)) {
            return Type.Float;

        } else if (Boolean.class.isAssignableFrom(type)
                || boolean.class.isAssignableFrom(type)) {
            return Type.Boolean;

        } else if (Date.class.isAssignableFrom(type)) {
            return Type.Date;
        }
        return null;
    }

    enum Type {
        String, Long, Int, Double, Float, Boolean, Date
    }

    private static Logger logger = Logger.getLogger(AnnoMember.class);
}