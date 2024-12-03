package test.tables;

import java.util.List;

public abstract class AbstractRepository<Message> {
  public abstract Message createTable();
  public abstract List<Message> getAll();
  public abstract Message getById(Message message);
  public abstract Message update(Message entity);
  public abstract Message delete(Message message);
  public abstract Message create(Message entity);
}
