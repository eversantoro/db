package br.tec.db.service;

import br.tec.db.api.dto.AccountRequest;
import br.tec.db.api.dto.AccountResponse;
import br.tec.db.api.dto.TransactionResponse;
import br.tec.db.domain.model.Account;
import br.tec.db.domain.repository.AccountRepository;
import br.tec.db.domain.repository.FinancialTransactionRepository;
import br.tec.db.service.exception.DuplicateResourceException;
import br.tec.db.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Casos de uso de gestão e consulta de contas.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final FinancialTransactionRepository transactionRepository;

    public AccountService(
            AccountRepository accountRepository,
            FinancialTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Cadastra nova conta com saldo inicial informado.
     */
    @Transactional
    public AccountResponse create(AccountRequest request) {
        if (accountRepository.existsById(request.id())) {
            throw new DuplicateResourceException("Conta já cadastrada: " + request.id());
        }
        Account account = new Account(request.id(), request.name(), request.initialBalance());
        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public AccountResponse findById(String id) {
        return accountRepository.findById(id)
                .map(AccountResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(AccountResponse::from)
                .toList();
    }

    /**
     * Lista movimentações financeiras da conta, da mais recente para a mais antiga.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> listTransactions(String accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Conta não encontrada: " + accountId);
        }
        return transactionRepository.findByAccountIdOrderByOccurredAtDesc(accountId).stream()
                .map(TransactionResponse::from)
                .toList();
    }
}
