package application.repository;

import application.entity.TelegramToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramTokenRepository extends JpaRepository<TelegramToken, Integer> {

}
