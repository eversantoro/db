package br.tec.db.domain.repository;

import br.tec.db.domain.model.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Persistência de contas com suporte a bloqueio pessimista em transferências concorrentes.
 */
public interface AccountRepository extends JpaRepository<Account, String> {

    /**
     * Carrega a conta com lock exclusivo (SELECT FOR UPDATE) para operações de transferência.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") String id);
}
