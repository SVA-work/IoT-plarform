package test.DAO;

import test.entity.User;

import java.util.List;

public class UserController extends AbstractController<User, Integer> {

  @Override
  public List<User> getAll() {
    return null;
  }

  @Override
  public User getById(Integer id) {
    return null;
  }

  @Override
  public User update(User user) {
    return null;
  }

  @Override
  public boolean delete(Integer id) {
    return false;
  }

  @Override
  public boolean create(User user) {
    return false;
  }
}
