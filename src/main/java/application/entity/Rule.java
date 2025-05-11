package application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    private String ruleType;

    @Column(name = "value", nullable = false)
    private Double value;

    @Column(name = "comparison", nullable = false)
    private String comparison;

    @ManyToOne
    @JoinColumn(name = "device_id", foreignKey = @ForeignKey(name = "fk_rules_device_id"))
    private Device device;
}
