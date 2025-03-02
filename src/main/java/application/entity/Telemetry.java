package application.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "telemetry")
public class Telemetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "temperature")
    private String temperature;

    @Column(name = "humidity")
    private String humidity;

    @Column(name = "pressure", nullable = false, unique = true)
    private String pressure;

    @Column(name = "aqi", nullable = false)
    private String aqi;

    @Column(name = "rssi")
    private String rssi;

    @Column(name = "snr")
    private String snr;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "device_id", foreignKey = @ForeignKey(name = "fk_telemetry_device_id"))
    private Device device;

    @PrePersist
    protected void onCreate() {
        time = LocalDateTime.now();
    }
}
