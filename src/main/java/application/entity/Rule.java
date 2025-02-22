package application.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "rules")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "rule", nullable = false)
    private String rule;

    @ManyToOne
    @JoinColumn(name = "device_id", foreignKey = @ForeignKey(name = "fk_rules_device_id"))
    private Device device;
}
