package br.tec.db.api;

import br.tec.db.api.dto.TransferRequest;
import br.tec.db.api.dto.TransferResponse;
import br.tec.db.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de transferência de fundos entre contas.
 */
@RestController
@RequestMapping("/api/v1/transfers")
@Tag(name = "Transferências", description = "Transferência de valores entre contas")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Transferir fundos", description = "Debita origem, credita destino e registra movimentações")
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request) {
        return transferService.transfer(request);
    }
}
