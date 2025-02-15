package application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import application.entity.Device;
import application.entity.Rule;

public interface RuleRepository extends JpaRepository<Rule, Integer> {

    Optional<Rule> findByRuleAndDevice(String rule, Device device);
}