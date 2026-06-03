package br.tec.db.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Página HTML de boas-vindas com links (db.tec.br).
 * O redirect da raiz {@code /} está em {@link br.tec.db.config.WebMvcConfig}.
 */
@RestController
public class HomeController {

    @GetMapping(value = "/home", produces = MediaType.TEXT_HTML_VALUE)
    public String homePage() {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8"/>
                    <meta http-equiv="refresh" content="0;url=/swagger-ui/index.html"/>
                    <title>API Banco Digital — db.tec.br</title>
                </head>
                <body>
                    <h1>API Banco Digital</h1>
                    <p>Redirecionando para o Swagger…</p>
                    <ul>
                        <li><a href="/swagger-ui/index.html">Swagger UI</a></li>
                        <li><a href="/api/v1/accounts">Listar contas (JSON)</a></li>
                        <li><a href="/api-docs">OpenAPI</a></li>
                    </ul>
                </body>
                </html>
                """;
    }
}
