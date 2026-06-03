package br.tec.db.domain.repository;

import br.tec.db.domain.model.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Consulta de movimentações financeiras por conta.
 */
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, java.util.UUID> {

    List<FinancialTransaction> findByAccountIdOrderByOccurredAtDesc(String accountId);
}
