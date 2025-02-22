package application.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "telegram_tokens_id", foreignKey = @ForeignKey(name = "fk_users_telegramToken_id"))
    private TelegramToken telegramToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Device> devices;
}