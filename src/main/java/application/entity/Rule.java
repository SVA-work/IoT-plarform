package application.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "rules")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "rule", nullable = false)
    private String rule;

    @Column(name = "lowestValue", nullable = false)
    private Integer lowestValue;

    @Column(name = "highestValue", nullable = false)
    private Integer highestValue;

    @ManyToOne
    @JoinColumn(name = "device_id", foreignKey = @ForeignKey(name = "fk_rules_device_id"))
    private Device device;
}
