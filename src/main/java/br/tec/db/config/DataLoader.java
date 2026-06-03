package br.tec.db.config;

import br.tec.db.domain.model.Account;
import br.tec.db.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Pré-carrega contas de demonstração quando o banco está vazio.
 */
@Configuration
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner seedAccounts(AccountRepository accountRepository) {
        return args -> {
            if (accountRepository.count() > 0) {
                return;
            }
            log.info("Carregando contas iniciais de demonstração (db.tec.br)...");

            accountRepository.save(new Account("conta-001", "Ana Costa", new BigDecimal("5000.00")));
            accountRepository.save(new Account("conta-002", "Bruno Lima", new BigDecimal("3200.50")));
            accountRepository.save(new Account("conta-003", "Carla Mendes", new BigDecimal("150.00")));

            log.info("3 contas de demonstração criadas.");
        };
    }
}
