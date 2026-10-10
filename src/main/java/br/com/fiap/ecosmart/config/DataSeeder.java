package br.com.fiap.ecosmart.config;

import br.com.fiap.ecosmart.model.Alerta;
import br.com.fiap.ecosmart.model.Sensor;
import br.com.fiap.ecosmart.model.Unidade;
import br.com.fiap.ecosmart.repository.AlertaRepository;
import br.com.fiap.ecosmart.repository.SensorRepository;
import br.com.fiap.ecosmart.repository.UnidadeRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Carrega dados de exemplo (apenas se ecosmart.seed=true e a collection estiver vazia). */
@Component
public class DataSeeder implements CommandLineRunner {

    private final boolean seed;
    private final SensorRepository sensores;
    private final AlertaRepository alertas;
    private final UnidadeRepository unidades;

    public DataSeeder(@Value("${ecosmart.seed:false}") boolean seed,
                      SensorRepository sensores,
                      AlertaRepository alertas,
                      UnidadeRepository unidades) {
        this.seed = seed;
        this.sensores = sensores;
        this.alertas = alertas;
        this.unidades = unidades;
    }

    @Override
    public void run(String... args) {
        if (!seed) {
            return;
        }
        if (unidades.count() == 0) {
            unidades.save(unidade("UNI001", "Residencial Palmeiras", "residencial", "num_moradores", 48));
            unidades.save(unidade("UNI002", "Comercio Jardim Verde", "comercial", "num_colaboradores", 22));
            unidades.save(unidade("UNI005", "Predio Publico Central", "publico", "num_colaboradores", 130));
        }
        if (sensores.count() == 0) {
            sensores.save(sensor("SENS001", "medidor_energia", "UNI001", "potencia_kw", 4.01));
            sensores.save(sensor("SENS003", "qualidade_ar", "UNI002", "pm25_ugm3", 18.4));
            sensores.save(sensor("SENS005", "ocupacao", "UNI005", "pessoas_detectadas", 3));
        }
        if (alertas.count() == 0) {
            Alerta a = new Alerta();
            a.setId("ALT002");
            a.setSensorId("SENS001");
            a.setUnidadeId("UNI001");
            a.setTipoAlerta("consumo_energia_excedido");
            a.setSeveridade("alta");
            a.setStatus("aberto");
            a.getAtributos().put("acao_automatica_executada", "notificacao_enviada_ao_gestor");
            alertas.save(a);
        }
    }

    private Unidade unidade(String id, String nome, String tipo, String chave, Object valor) {
        Unidade u = new Unidade();
        u.setId(id);
        u.setNome(nome);
        u.setTipo(tipo);
        u.setAtributos(new HashMap<>(Map.of(chave, valor)));
        return u;
    }

    private Sensor sensor(String id, String tipo, String unidadeId, String chave, Object valor) {
        Sensor s = new Sensor();
        s.setId(id);
        s.setTipo(tipo);
        s.setUnidadeId(unidadeId);
        s.setStatus("ativo");
        s.setAtributos(new HashMap<>(Map.of(chave, valor)));
        return s;
    }
}
