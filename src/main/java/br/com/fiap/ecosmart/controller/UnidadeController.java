package br.com.fiap.ecosmart.controller;

import br.com.fiap.ecosmart.model.Unidade;
import br.com.fiap.ecosmart.repository.UnidadeRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/unidades")
public class UnidadeController extends BaseController<Unidade> {

    public UnidadeController(UnidadeRepository repository) {
        super(repository);
    }
}
