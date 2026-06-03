package br.tec.db.api;

import br.tec.db.api.dto.AccountRequest;
import br.tec.db.api.dto.AccountResponse;
import br.tec.db.api.dto.TransactionResponse;
import br.tec.db.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints de gestão e consulta de contas bancárias.
 */
@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Contas", description = "Cadastro e consulta de contas")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar conta", description = "Cria conta com ID, nome e saldo inicial")
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar contas", description = "Retorna todas as contas cadastradas")
    public List<AccountResponse> list() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta por ID")
    public AccountResponse findById(@PathVariable String id) {
        return accountService.findById(id);
    }

    @GetMapping("/{id}/transactions")
    @Operation(summary = "Movimentações da conta", description = "Extrato ordenado do mais recente ao mais antigo")
    public List<TransactionResponse> listTransactions(@PathVariable String id) {
        return accountService.listTransactions(id);
    }
}
