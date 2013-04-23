package me.shakiba.jdbi.annotation;

import java.lang.annotation.Annotation;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;

public class BindAnnoFactory implements BinderFactory {

    @Override
    public Binder<BindAnno, Object> build(Annotation annotation) {
        return new Binder<BindAnno, Object>() {
            public void bind(SQLStatement<?> q, BindAnno bind, Object arg) {
                final String prefix;
                if ("___jdbi_bare___".equals(bind.value())) {
                    prefix = "";
                } else {
                    prefix = bind.value() + ".";
                }

                try {
                    AnnoClass<?> annos = AnnoClass.get(arg.getClass());
                    for (AnnoMember anno : annos.getters()) {
                        q.bind(prefix + anno.getName(), anno.read(arg));
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(
                            "unable to bind bean properties", e);
                }

            }
        };
    }
}
