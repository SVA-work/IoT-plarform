package application.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "telegram_tokens")
public class TelegramToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "token", nullable = false)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_telegram_tokens_user_id"))
    private User user;
}
