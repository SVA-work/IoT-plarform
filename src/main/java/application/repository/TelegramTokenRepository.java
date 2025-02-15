package application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import application.entity.TelegramToken;

public interface TelegramTokenRepository extends JpaRepository<TelegramToken, Integer> {
    
}
