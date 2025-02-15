package application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import application.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    
    boolean existsByLogin(String login);
    Optional<User> findByLogin(String login);
}
