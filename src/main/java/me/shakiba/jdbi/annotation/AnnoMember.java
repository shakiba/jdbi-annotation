package me.shakiba.jdbi.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.persistence.Column;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static Type typeOf(Class<?> clazz) {
        for (Type type : Type.primitives) {
            if (type.isAssignableFrom(clazz)) {
                return type;
            }
        }
        throw new IllegalArgumentException();
    }

    public abstract static class Type {
        public static Type String = new Type() {
            @Override
            public boolean isAssignableFrom(Class<?> clazz) {
                return String.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getValue(ResultSet rs, String name)
                    throws SQLException {
                return rs.getString(name);
            }
        };
        public static Type Long = new Type() {
            @Override
            public boolean isAssignableFrom(Class<?> clazz) {
                return Long.class.isAssignableFrom(clazz)
                        || long.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getValue(ResultSet rs, String name)
                    throws SQLException {
                return rs.getLong(name);
            }
        };
        public static Type Int = new Type() {
            @Override
            public boolean isAssignableFrom(Class<?> clazz) {
                return Integer.class.isAssignableFrom(clazz)
                        || int.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getValue(ResultSet rs, String name)
                    throws SQLException {
                return rs.getInt(name);
            }
        };
        public static Type Double = new Type() {
            @Override
            public boolean isAssignableFrom(Class<?> clazz) {
                return Double.class.isAssignableFrom(clazz)
                        || double.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getValue(ResultSet rs, String name)
                    throws SQLException {
                return rs.getDouble(name);
            }
        };
        public static Type Float = new Type() {
            @Override
            public boolean isAssignableFrom(Class<?> clazz) {
                return Float.class.isAssignableFrom(clazz)
                        || float.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getValue(ResultSet rs, String name)
                    throws SQLException {
                return rs.getFloat(name);
            }
        };
        public static Type Boolean = new Type() {
            @Override
            public boolean isAssignableFrom(Class<?> clazz) {
                return Boolean.class.isAssignableFrom(clazz)
                        || boolean.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getValue(ResultSet rs, String name)
                    throws SQLException {
                return rs.getBoolean(name);
            }
        };
        public static Type Date = new Type() {
            @Override
            public boolean isAssignableFrom(Class<?> clazz) {
                return Date.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getValue(ResultSet rs, String name)
                    throws SQLException {
                return rs.getDate(name);
            }
        };
        public static Type[] primitives = { String, Long, Int, Double, Float,
                Boolean, Date };

        public abstract boolean isAssignableFrom(Class<?> type);

        public abstract Object getValue(ResultSet rs, String name)
                throws SQLException;

    }

    private static Logger logger = LoggerFactory.getLogger(AnnoMember.class);
}