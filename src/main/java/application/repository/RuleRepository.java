package application.repository;

import application.entity.Device;
import application.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RuleRepository extends JpaRepository<Rule, Integer> {

    Optional<Rule> findByRuleAndValueAndComparisonAndDevice(
        String rule,
        Double value,
        String comparison,
        Device device
    );
}