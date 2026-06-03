package br.tec.db.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do Swagger/OpenAPI para documentação interativa da API.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bancoDigitalOpenApi() {
        return new OpenAPI()
                .servers(List.of(new Server().url("/").description("Servidor atual (mesma origem do Swagger)")))
                .info(new Info()
                        .title("API Banco Digital")
                        .description("Transferência de valores entre contas e consulta de movimentações financeiras.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DB Tecnologia")
                                .url("https://db.tec.br"))
                        .license(new License().name("Uso interno - desafio técnico")));
    }
}
