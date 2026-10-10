package br.com.fiap.ecosmart.controller;

import br.com.fiap.ecosmart.model.Conformidade;
import br.com.fiap.ecosmart.repository.ConformidadeRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conformidade")
public class ConformidadeController extends BaseController<Conformidade> {

    public ConformidadeController(ConformidadeRepository repository) {
        super(repository);
    }
}
