package test.DAO;

import java.util.List;

public abstract class AbstractController <Entity, Integer> {
  public abstract List<Entity> getAll();
  public abstract Entity getById(Integer id);
  public abstract Entity update(Entity entity);
  public abstract boolean delete(Integer id);
  public abstract boolean create(Entity entity);
}