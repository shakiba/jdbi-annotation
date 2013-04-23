package me.shakiba.jdbi.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnoClass<C> {

    private final List<AnnoMember> setters = new ArrayList<AnnoMember>();
    private final List<AnnoMember> getters = new ArrayList<AnnoMember>();

    // TODO: cache them!
    public static <C> AnnoClass<C> get(Class<C> clazz) {
        return new AnnoClass<C>(clazz);
    }

    private AnnoClass(Class<C> clazz) {
        if (logger.isDebugEnabled()) {
            logger.debug("init " + clazz);
        }

        for (Field member : clazz.getDeclaredFields()) {
            if (member.getAnnotation(Column.class) != null) {
                setters.add(new AnnoMember(clazz, member));
                getters.add(new AnnoMember(clazz, member));
            }
        }
        for (Method member : clazz.getDeclaredMethods()) {
            if (member.getAnnotation(Column.class) == null) {
                continue;
            }
            if (member.getParameterTypes().length == 1) {
                setters.add(new AnnoMember(clazz, member));
            } else if (member.getParameterTypes().length == 0) {
                getters.add(new AnnoMember(clazz, member));
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("init " + clazz + ": " + setters.size()
                    + " setters and " + getters.size() + " getters.");
        }
    }

    public List<AnnoMember> setters() {
        return setters;
    }

    public List<AnnoMember> getters() {
        return getters;
    }

    private static Logger logger = LoggerFactory.getLogger(AnnoClass.class);
}