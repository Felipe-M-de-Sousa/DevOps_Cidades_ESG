package br.com.fiap.ecosmart.controller;

import br.com.fiap.ecosmart.model.Leitura;
import br.com.fiap.ecosmart.repository.LeituraRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leituras")
public class LeituraController extends BaseController<Leitura> {

    public LeituraController(LeituraRepository repository) {
        super(repository);
    }
}
