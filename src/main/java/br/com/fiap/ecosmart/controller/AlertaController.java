package br.com.fiap.ecosmart.controller;

import br.com.fiap.ecosmart.model.Alerta;
import br.com.fiap.ecosmart.repository.AlertaRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController extends BaseController<Alerta> {

    private final AlertaRepository alertaRepository;

    public AlertaController(AlertaRepository repository) {
        super(repository);
        this.alertaRepository = repository;
    }

    /** Alertas que ainda precisam de acao (ex.: consumo acima do limite). */
    @GetMapping("/abertos")
    public List<Alerta> abertos() {
        return alertaRepository.findByStatus("aberto");
    }
}
