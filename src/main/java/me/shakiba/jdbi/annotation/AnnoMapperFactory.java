package me.shakiba.jdbi.annotation;

import org.skife.jdbi.v2.ResultSetMapperFactory;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@SuppressWarnings("rawtypes")
public class AnnoMapperFactory implements ResultSetMapperFactory {

    @Override
    public boolean accepts(Class clazz, StatementContext ctx) {
        return AnnoMapper.accept(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultSetMapper mapperFor(Class clazz, StatementContext ctx) {
        return new AnnoMapper(clazz);
    }
}