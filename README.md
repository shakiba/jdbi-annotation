JPA annotation extension for jDBI
===============

### Usage

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

Use `AnnoMapper` to create `ResultSetMapper` for your entity to use with [jDBI](/brianm/jdbi/):

```java
ResultSetMapper<Something> mapper = AnnoMapper.get(Something.class);
```