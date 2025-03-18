package application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ServerConfig {

    public static String BOT_TOKEN;
    public static int SERVER_TOKEN;

    public ServerConfig(@Value("${bot.token}") String token, @Value("${server.port}") int port) {
        BOT_TOKEN = token;
        SERVER_TOKEN = 8092;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);  // Настройте размер пула потоков
        scheduler.setThreadNamePrefix("ScheduledTask-");
        scheduler.initialize();
        return scheduler;
    }
}
