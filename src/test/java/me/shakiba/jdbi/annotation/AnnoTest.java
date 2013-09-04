package me.shakiba.jdbi.annotation;

import java.util.List;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class AnnoTest extends Assert {

    public void test() throws Exception {
        Something brian = new Something(1, "Brian", null);
        Something keith = new Something(2, "Keith", 7777777777777L);

        DBI dbi = new DBI("jdbc:h2:mem:test");
        Handle h = dbi.open();
        h.execute("create table something (id int primary key, name varchar(100), value bigint)");

        SomethingDAO dao = dbi.open(SomethingDAO.class);
        dao.insert(brian);
        dao.insert(keith);

        List<Something> rs = h
                .createQuery("select * from something order by id")
                .map(AnnoMapper.get(Something.class)).list();

        assertEquals(rs.size(), 2);
        assertEquals(rs.get(0), brian);
        assertEquals(rs.get(1), keith);

        h.close();
    }

    @Test(enabled = false)
    static public void assertEquals(Something actual, Something expected) {
        assertEquals(actual.id(), expected.id());
        assertEquals(actual.name(), expected.name());
        assertEquals(actual.value, expected.value);
    }
}
