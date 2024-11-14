package test.DAO;

import test.entity.Device;

import java.util.List;

public class DeviceController extends AbstractController<Device, Integer> {
  @Override
  public List<Device> getAll() {
    return null;
  }

  @Override
  public Device getById(Integer id) {
    return null;
  }

  @Override
  public Device update(Device device) {
    return null;
  }

  @Override
  public boolean delete(Integer id) {
    return false;
  }

  @Override
  public boolean create(Device device) {
    return false;
  }
}
