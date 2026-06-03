package br.tec.db.service;

import br.tec.db.api.dto.TransferRequest;
import br.tec.db.api.dto.TransferResponse;
import br.tec.db.domain.model.Account;
import br.tec.db.domain.model.FinancialTransaction;
import br.tec.db.domain.model.TransactionType;
import br.tec.db.domain.repository.AccountRepository;
import br.tec.db.domain.repository.FinancialTransactionRepository;
import br.tec.db.notification.TransferCompletedEvent;
import br.tec.db.service.exception.BusinessException;
import br.tec.db.service.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Orquestra transferências entre contas com consistência transacional e lock pessimista.
 */
@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TransferService(
            AccountRepository accountRepository,
            FinancialTransactionRepository transactionRepository,
            ApplicationEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Transfere valor entre contas, registra movimentações e dispara notificação após commit.
     */
    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        validateTransferRequest(request);

        List<String> orderedIds = List.of(request.fromAccountId(), request.toAccountId());
        orderedIds = orderedIds.stream().sorted(Comparator.naturalOrder()).toList();

        Account first = loadAccountForUpdate(orderedIds.get(0));
        Account second = loadAccountForUpdate(orderedIds.get(1));

        Account from = request.fromAccountId().equals(first.getId()) ? first : second;
        Account to = request.toAccountId().equals(first.getId()) ? first : second;

        from.debit(request.amount());
        to.credit(request.amount());

        accountRepository.save(from);
        accountRepository.save(to);

        UUID transferId = UUID.randomUUID();
        String description = "Transferência " + transferId;

        transactionRepository.save(new FinancialTransaction(
                from.getId(),
                TransactionType.DEBIT,
                request.amount(),
                from.getBalance(),
                description,
                transferId));

        transactionRepository.save(new FinancialTransaction(
                to.getId(),
                TransactionType.CREDIT,
                request.amount(),
                to.getBalance(),
                description,
                transferId));

        TransferResponse response = new TransferResponse(
                transferId,
                from.getId(),
                to.getId(),
                request.amount(),
                Instant.now());

        eventPublisher.publishEvent(new TransferCompletedEvent(
                transferId,
                from.getId(),
                from.getName(),
                to.getId(),
                to.getName(),
                request.amount()));

        return response;
    }

    private void validateTransferRequest(TransferRequest request) {
        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new BusinessException("Conta de origem e destino devem ser diferentes");
        }
    }

    private Account loadAccountForUpdate(String accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + accountId));
    }
}
