package me.shakiba.jdbi.annotation;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.Entity;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnoMapper<C> implements ResultSetMapper<C> {

    private final Class<C> clazz;
    private final AnnoClass<C> annos;

    public static boolean accept(Class<?> clazz) {
        if (logger.isDebugEnabled()) {
            logger.debug("accept " + clazz);
        }
        return clazz.getAnnotation(Entity.class) != null;
    }

    public static <C> AnnoMapper<C> get(Class<C> clazz) {
        return new AnnoMapper<C>(clazz);
    }

    private AnnoMapper(Class<C> clazz) {
        if (logger.isDebugEnabled()) {
            logger.debug("init " + clazz);
        }
        this.clazz = clazz;
        this.annos = AnnoClass.get(clazz);
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
            for (AnnoMember annoMember : annos.setters()) {
                annoMember.write(obj, get(annoMember, rs, ctx));
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    private Object get(AnnoMember annoMember, ResultSet rs, StatementContext ctx)
            throws SQLException {
        AnnoType annoType = annoMember.getType();
        String name = annoMember.getName();
        Object value = annoType.getValue(rs, name);
        return rs.wasNull() ? null : value;
    }

    private static Logger logger = LoggerFactory.getLogger(AnnoMapper.class);
}