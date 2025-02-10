package application.repository;

import application.dto.DbConnectionDto;
import application.dto.objects.DeviceDto;
import application.dto.objects.RuleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DevicesRepository extends AbstractRepository<DeviceDto> {
    private static final Logger LOG = LoggerFactory.getLogger(DevicesRepository.class);
    private final String tableName = "devices";
    private final String tableID = "device_id";
    private final DbConnectionDto dbConnectionDto;

    public DevicesRepository(DbConnectionDto dbConnectionDto) {
        this.dbConnectionDto = dbConnectionDto;
    }

    @Override
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS "
                + tableName + "("
                + "device_id SERIAL PRIMARY KEY,"
                + "user_id INTEGER REFERENCES users(user_id),"
                + "token VARCHAR(255) NOT NULL,"
                + "type VARCHAR(255) NOT NULL)";
        try {
            Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            LOG.error("Соединение не удалось", e);
        }
    }

    @Override
    public List<DeviceDto> getAll() {
        String sql = "SELECT * FROM " + tableName;
        List<DeviceDto> list = new ArrayList<>();
        try {
            Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                DeviceDto response = new DeviceDto();
                response.setDeviceId(resultSet.getString(tableID));
                response.setUserId(resultSet.getString("user_id"));
                response.setToken(resultSet.getString("token"));
                response.setType(resultSet.getString("type"));
                list.add(response);
            }
            resultSet.close();
        } catch (SQLException e) {
            LOG.error("Не удалось получить информацию из таблицы devices", e);
        }
        return list;
    }

    @Override
    public DeviceDto getById(DeviceDto deviceDto) {
        int id = Integer.parseInt(deviceDto.getDeviceId());
        String sql = "SELECT * FROM " + tableName + " WHERE " + tableID + " = ?";
        DeviceDto response = new DeviceDto();
        try {
            Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                response.setDeviceId(resultSet.getString(tableID));
                response.setUserId(resultSet.getString("user_id"));
                response.setToken(resultSet.getString("token"));
                response.setType(resultSet.getString("type"));
            }
            resultSet.close();
        } catch (SQLException e) {
            LOG.error("Не удалось получить информацию об устройстве", e);
        }
        return response;
    }

    public List<RuleDto> rulesOfDevice(DeviceDto message) {
        int id = Integer.parseInt(message.getDeviceId());
        String sql = "SELECT r.* "
                + "FROM rules r "
                + "JOIN devices d ON r.device_id = d.device_id "
                + "WHERE d.device_id = ?";
        List<RuleDto> list = new ArrayList<>();
        try {
            Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                RuleDto response = new RuleDto();
                response.setDeviceId(resultSet.getString("rule_id"));
                response.setRule(resultSet.getString("rule"));
                list.add(response);
            }
            resultSet.close();
        } catch (SQLException e) {
            LOG.error("Не удалось получить информацию об правилах устройства", e);
        }
        return list;
    }

    @Override
    public DeviceDto delete(DeviceDto message) {
        int id = Integer.parseInt(message.getDeviceId());
        DeviceDto response = new DeviceDto();
        String sql = "DELETE FROM " + tableName + " WHERE " + tableID + " = ?";
        try {
            Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            response.setSuccessful(true);
        } catch (SQLException e) {
            LOG.error("Не удалось удалить устройство", e);
            response.setSuccessful(false);
        }
        return response;
    }

    @Override
    public DeviceDto create(DeviceDto entity) {
        String sql = "INSERT INTO " + tableName + " (user_id, token, type) VALUES (?, ?, ?)";
        DeviceDto response = new DeviceDto();
        try {
            Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, Integer.parseInt(entity.getUserId()));
            preparedStatement.setString(2, entity.getToken());
            preparedStatement.setString(3, entity.getType());
            preparedStatement.executeUpdate();
            response.setSuccessful(true);
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                long deviceId = generatedKeys.getLong(1);
                response.setDeviceId(String.valueOf(deviceId));
            }
        } catch (SQLException e) {
            LOG.error("Не удалось создать устройство", e);
            response.setSuccessful(false);
        }
        return response;
    }
}
