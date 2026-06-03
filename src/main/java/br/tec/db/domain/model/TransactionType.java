package br.tec.db.domain.model;

/**
 * Tipo de movimentação financeira registrada no extrato da conta.
 */
public enum TransactionType {
    /** Entrada de valor (crédito). */
    CREDIT,
    /** Saída de valor (débito). */
    DEBIT
}
