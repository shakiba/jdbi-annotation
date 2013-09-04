package me.shakiba.jdbi.annotation;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

@RegisterMapperFactory(AnnoMapperFactory.class)
public interface SomethingDAO {

    @SqlUpdate("insert into something (id, name, value) values (:id, :name, :value)")
    void insert(@BindAnno Something something);

}