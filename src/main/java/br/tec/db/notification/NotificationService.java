package br.tec.db.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Envia notificação ao cliente após transferência concluída.
 * <p>
 * Em produção, integraria e-mail, SMS ou push; aqui simula o envio via log estruturado.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Notifica origem e destino de forma assíncrona, sem bloquear a resposta HTTP.
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferCompleted(TransferCompletedEvent event) {
        log.info(
                "[NOTIFICAÇÃO db.tec.br] Transferência {} concluída: {} ({}) -> {} ({}), valor R$ {}",
                event.transferId(),
                event.fromAccountId(),
                event.fromAccountHolderName(),
                event.toAccountId(),
                event.toAccountHolderName(),
                event.amount());

        log.info(
                "[NOTIFICAÇÃO db.tec.br] Cliente '{}' informado sobre débito de R$ {}",
                event.fromAccountHolderName(),
                event.amount());

        log.info(
                "[NOTIFICAÇÃO db.tec.br] Cliente '{}' informado sobre crédito de R$ {}",
                event.toAccountHolderName(),
                event.amount());
    }
}
