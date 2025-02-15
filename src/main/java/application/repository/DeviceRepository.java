package application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import application.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, Integer> {

    Optional<Device> findByUuid(String uuid);
    
}