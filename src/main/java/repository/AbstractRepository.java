package repository;

import java.util.List;

public abstract class AbstractRepository<Message> {
    public abstract void createTable();

    public abstract List<Message> getAll();

    public abstract Message getById(Message message);

    public abstract Message delete(Message message);

    public abstract Message create(Message entity);
}
