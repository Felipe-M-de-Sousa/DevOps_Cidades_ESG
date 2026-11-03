package br.com.fiap.ecosmart.support;

import br.com.fiap.ecosmart.model.EcoDocument;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio em memoria usado nos testes de API/BDD. Cria, via proxy, uma implementacao
 * da interface do repositorio (ex.: SensorRepository) que guarda os documentos em um mapa.
 * Assim os testes sao rapidos e nao dependem de MongoDB nem de Docker.
 */
public final class InMemoryRepository {

    private InMemoryRepository() {
    }

    @SuppressWarnings("unchecked")
    public static <R> R of(Class<R> repositoryType) {
        Map<String, EcoDocument> store = new LinkedHashMap<>();
        InvocationHandler handler = (proxy, method, args) -> handle(proxy, method, args, store);
        return (R) Proxy.newProxyInstance(
                repositoryType.getClassLoader(), new Class<?>[] {repositoryType}, handler);
    }

    private static Object handle(Object proxy, Method method, Object[] args,
                                 Map<String, EcoDocument> store) throws Exception {
        switch (method.getName()) {
            case "save": {
                EcoDocument doc = (EcoDocument) args[0];
                if (doc.getId() == null || doc.getId().isBlank()) {
                    // simula o ObjectId gerado pelo MongoDB (24 caracteres)
                    doc.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 24));
                }
                store.put(doc.getId(), doc);
                return doc;
            }
            case "findAll":
                return new ArrayList<>(store.values());
            case "findById":
                return Optional.ofNullable(store.get((String) args[0]));
            case "existsById":
                return store.containsKey((String) args[0]);
            case "deleteById":
                store.remove((String) args[0]);
                return null;
            case "count":
                return (long) store.size();
            case "findByStatus": {
                String status = (String) args[0];
                List<EcoDocument> encontrados = new ArrayList<>();
                for (EcoDocument doc : store.values()) {
                    Object atual = doc.getClass().getMethod("getStatus").invoke(doc);
                    if (status.equals(atual)) {
                        encontrados.add(doc);
                    }
                }
                return encontrados;
            }
            case "toString":
                return "InMemoryRepository";
            case "hashCode":
                return System.identityHashCode(proxy);
            case "equals":
                return proxy == args[0];
            default:
                throw new UnsupportedOperationException("Metodo nao suportado no teste: " + method.getName());
        }
    }
}
