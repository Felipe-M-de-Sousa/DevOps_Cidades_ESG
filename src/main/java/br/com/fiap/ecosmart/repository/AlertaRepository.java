package br.com.fiap.ecosmart.repository;

import br.com.fiap.ecosmart.model.Alerta;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlertaRepository extends MongoRepository<Alerta, String> {
    List<Alerta> findByStatus(String status);
}
