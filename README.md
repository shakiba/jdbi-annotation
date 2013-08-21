JPA annotation extension for jDBI
===============

Extension for [jDBI](https://github.com/brianm/jdbi/) to use JPA annotation for mapping/binding instead of JavaBeans conventions.

### Usage

#### Annotate

Annotate your entity with JPA annotations:

```java
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Something {
    @Column
    private int id;
    @Column
    private String name;
}
```

#### Map

Use `AnnoMapper` to create `ResultSetMapper`:

```java
ResultSetMapper<Something> mapper = AnnoMapper.get(Something.class);
```

Or register `AnnoMapperFactory` as a `ResultSetMapperFactory`:

```java
@RegisterMapperFactory(AnnoMapperFactory.class)
public interface SomethingDAO {

    @SqlQuery("select * from Something where id = :id")
    Something get(@Bind("id") long id);

}
```
#### Bind

Use `@BindAnno` instead of `@BindBean` to bind annotated classes.

```java
public interface SomethingDAO {

    @SqlUpdate("update something set name = :s.name where id = :s.id")
    void update(@BindAnno("s") Something something);

}
```

### Maven

```xml
<dependency>
    <groupId>me.shakiba.jdbi</groupId>
    <artifactId>jdbi-annotation</artifactId>
    <version>0.1.1</version>
</dependency>
```
