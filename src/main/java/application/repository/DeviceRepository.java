package application.repository;

import application.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Integer> {

    Optional<Device> findByUuid(String uuid);

    Optional<Device> findByDeviceNameAndUserId(String deviceName, Integer userId);

    @Query("SELECT DISTINCT d FROM Device d "
            +"LEFT JOIN FETCH d.rules "
            + "LEFT JOIN FETCH d.user u "
            + "LEFT JOIN FETCH u.telegramToken "
            + "WHERE d.uuid = :uuid")
    Optional<Device> findByUuidWithRulesAndToken(@Param("uuid") String uuid);
}
