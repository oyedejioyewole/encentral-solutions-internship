package com.api.eventify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventifyApplication.class, args);
        System.out.println(
            String.format(
                "\n========================================\n" +
                    "🎉 Eventify API is running!\n" +
                    "📖 Swagger UI: http://localhost:8080/swagger-ui\n" +
                    "📄 API Docs: http://localhost:8080/api-docs\n" +
                    "🗄️  H2 Console: http://localhost:8080/h2-console\n" +
                    "========================================\n"
            )
        );
    }
}
