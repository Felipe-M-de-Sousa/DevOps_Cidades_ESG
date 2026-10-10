package br.com.fiap.ecosmart.repository;

import br.com.fiap.ecosmart.model.Sensor;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SensorRepository extends MongoRepository<Sensor, String> {
    List<Sensor> findByStatus(String status);
}
