package me.shakiba.jdbi.annotation;

import java.util.List;

import me.shakiba.jdbi.annotation.AnnoMapper;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class AnnoTest extends Assert {

    public void test() throws Exception {
        DBI dbi = new DBI("jdbc:h2:mem:test");
        Handle h = dbi.open();
        h.execute("create table something (id int primary key, name varchar(100))");
        h.execute("insert into something (id, name) values (1, 'Brian')");
        h.execute("insert into something (id, name) values (2, 'Keith')");

        List<Something> rs = h
                .createQuery("select * from something order by id")
                .map(AnnoMapper.get(Something.class)).list();

        assertEquals(rs.size(), 2);
        assertEquals(rs.get(0).getId(), 1);
        assertEquals(rs.get(0).getName(), "Brian");
        assertEquals(rs.get(1).getId(), 2);
        assertEquals(rs.get(1).getName(), "Keith");

        h.close();
    }
}
