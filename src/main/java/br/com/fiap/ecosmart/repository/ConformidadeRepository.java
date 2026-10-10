package br.com.fiap.ecosmart.repository;

import br.com.fiap.ecosmart.model.Conformidade;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConformidadeRepository extends MongoRepository<Conformidade, String> {
}
