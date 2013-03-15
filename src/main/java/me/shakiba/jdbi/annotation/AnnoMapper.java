package me.shakiba.jdbi.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnoMapper<C> implements ResultSetMapper<C> {

    private final Class<C> clazz;
    private final List<Anno> annos;

    public static boolean accept(Class<?> clazz) {
        if (logger.isDebugEnabled()) {
            logger.debug("accept " + clazz);
        }
        return clazz.getAnnotation(Entity.class) != null;
    }

    public static <C> AnnoMapper<C> get(Class<C> clazz) {
        return new AnnoMapper<C>(clazz);
    }

    public AnnoMapper(Class<C> clazz) {
        if (logger.isDebugEnabled()) {
            logger.debug("init " + clazz);
        }
        this.clazz = clazz;

        annos = new ArrayList<AnnoMapper<C>.Anno>();

        for (Field member : clazz.getDeclaredFields()) {
            if (member.getAnnotation(Column.class) != null) {
                annos.add(new Anno(member));
            }
        }
        for (Method member : clazz.getDeclaredMethods()) {
            if (member.getAnnotation(Column.class) != null
                    && member.getParameterTypes().length == 1) {
                annos.add(new Anno(member));
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("init " + clazz + "/" + annos.size());
        }
    }

    public boolean isEmpty() {
        return annos.isEmpty();
    }

    @Override
    public C map(int i, ResultSet rs, StatementContext ctx) throws SQLException {
        C obj;
        if (logger.isDebugEnabled()) {
            logger.debug("map " + clazz);
        }
        try {
            Constructor<C> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            obj = constructor.newInstance();
            for (Anno anno : annos) {
                anno.apply(obj, rs, ctx);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    private class Anno {

        private Column column;
        private String name;
        private Type type;
        private Field field;
        private Method method;

        public Anno(Field member) {
            this.field = member;
            column = member.getAnnotation(Column.class);
            name = nameOf(member, column);
            type = typeOf(member.getType());
        }

        public Anno(Method member) {
            this.method = member;
            column = member.getAnnotation(Column.class);
            name = nameOf(member, column);
            type = typeOf(member.getParameterTypes()[0]);
        }

        private void apply(C obj, ResultSet rs, StatementContext ctx)
                throws SQLException, IllegalArgumentException,
                IllegalAccessException, InvocationTargetException {
            if (logger.isDebugEnabled()) {
                logger.debug("apply " + clazz + "/" + name + "/" + type);
            }
            Object value = get(rs, ctx);
            if (logger.isDebugEnabled()) {
                logger.debug("apply " + clazz + "/" + name + "/" + type + "/"
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

        private Object get(ResultSet rs, StatementContext ctx)
                throws SQLException {
            switch (type) {
            case String:
                return rs.getString(name);
            case Long:
                return rs.getLong(name);
            case Int:
                return rs.getInt(name);
            case Double:
                return rs.getDouble(name);
            case Float:
                return rs.getFloat(name);
            case Boolean:
                return rs.getBoolean(name);
            case Date:
                return rs.getDate(name);
            default:
                break;
            }
            return null;
        }
    }

    private static String nameOf(Member member, Column column) {
        String name = column.name();
        if (name == null || name.length() == 0) {
            name = member.getName();
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

    private enum Type {
        String, Long, Int, Double, Float, Boolean, Date
    }

    private static Logger logger = LoggerFactory.getLogger(AnnoMapper.class);
}