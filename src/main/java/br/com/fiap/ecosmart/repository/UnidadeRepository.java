package br.com.fiap.ecosmart.repository;

import br.com.fiap.ecosmart.model.Unidade;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UnidadeRepository extends MongoRepository<Unidade, String> {
}
