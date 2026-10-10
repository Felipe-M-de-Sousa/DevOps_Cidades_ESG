package br.com.fiap.ecosmart.controller;

import br.com.fiap.ecosmart.model.Sensor;
import br.com.fiap.ecosmart.repository.SensorRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sensores")
public class SensorController extends BaseController<Sensor> {

    public SensorController(SensorRepository repository) {
        super(repository);
    }
}
