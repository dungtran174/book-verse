package com.bookverse.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Cấu hình OpenAPI 3.0 (Swagger) documentation.
 * Truy cập Swagger UI tại: http://localhost:{port}/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public OpenAPI bookVerseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BookVerse API")
                        .description("API hệ thống quản lý sách điện tử BookVerse. "
                                + "Hỗ trợ CRUD sách, upload/quản lý ảnh bìa, "
                                + "tìm kiếm full-text, và import hàng loạt.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("BookVerse Team")
                                .email("contact@bookverse.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")
                ));
    }
}
