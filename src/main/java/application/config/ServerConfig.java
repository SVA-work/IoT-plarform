package application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

  public static String botToken;

  public ServerConfig(@Value("${bot.token}") String token) {
    botToken = token;
  }
}
