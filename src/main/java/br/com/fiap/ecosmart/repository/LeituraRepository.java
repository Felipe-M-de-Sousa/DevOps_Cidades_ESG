package br.com.fiap.ecosmart.repository;

import br.com.fiap.ecosmart.model.Leitura;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LeituraRepository extends MongoRepository<Leitura, String> {
}
