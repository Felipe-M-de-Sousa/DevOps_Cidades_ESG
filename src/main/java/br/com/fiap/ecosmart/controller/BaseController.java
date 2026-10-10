package br.com.fiap.ecosmart.controller;

import br.com.fiap.ecosmart.model.EcoDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** CRUD generico reutilizado pelas 5 collections. */
public abstract class BaseController<T extends EcoDocument> {

    protected final MongoRepository<T, String> repository;

    protected BaseController(MongoRepository<T, String> repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<T> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> buscar(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<T> criar(@RequestBody T documento) {
        if (documento.getId() != null && documento.getId().isBlank()) {
            documento.setId(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(documento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<T> atualizar(@PathVariable String id, @RequestBody T documento) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        documento.setId(id);
        return ResponseEntity.ok(repository.save(documento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
