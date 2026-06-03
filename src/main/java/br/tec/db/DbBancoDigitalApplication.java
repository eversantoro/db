package br.tec.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Ponto de entrada da API REST do banco digital (db.tec.br).
 * <p>
 * Oferece gestão simplificada de contas, transferências entre contas,
 * consulta de movimentações e notificações pós-transferência.
 */
@SpringBootApplication
@EnableAsync
public class DbBancoDigitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbBancoDigitalApplication.class, args);
    }
}
