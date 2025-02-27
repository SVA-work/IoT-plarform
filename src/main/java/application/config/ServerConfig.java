package application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    public static String BOT_TOKEN;
    public static int SERVER_TOKEN;

    public ServerConfig(@Value("${bot.token}") String token, @Value("${server.port}") int port) {
        BOT_TOKEN = token;
        SERVER_TOKEN = port;
    }
}
