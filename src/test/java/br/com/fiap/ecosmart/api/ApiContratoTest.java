package br.com.fiap.ecosmart.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.fiap.ecosmart.support.ApiHarness;
import br.com.fiap.ecosmart.support.JsonContract;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Testes de API do CRUD das 5 collections: status code, corpo JSON e contrato (JSON Schema).
 * Cada teste e executado uma vez para cada recurso (sensores, leituras, alertas, unidades, conformidade).
 */
class ApiContratoTest {

    record Recurso(String nome, String caminho, String schema, String id, String corpo, String corpoAtualizado) {
        @Override
        public String toString() {
            return nome;
        }
    }

    static Stream<Recurso> recursos() {
        return Stream.of(
                new Recurso("sensores", "/api/sensores", "sensor.schema.json", "SENS900",
                        "{\"id\":\"SENS900\",\"tipo\":\"medidor_energia\",\"unidadeId\":\"UNI001\",\"status\":\"ativo\",\"atributos\":{\"potencia_kw\":4.01}}",
                        "{\"tipo\":\"medidor_energia\",\"unidadeId\":\"UNI001\",\"status\":\"manutencao\",\"atributos\":{\"potencia_kw\":0}}"),
                new Recurso("leituras", "/api/leituras", "leitura.schema.json", "LEIT900",
                        "{\"id\":\"LEIT900\",\"sensorId\":\"SENS900\",\"unidadeId\":\"UNI001\",\"tipoMedida\":\"energia_kwh\",\"valor\":96.4,\"timestamp\":\"2026-08-01T10:00:00Z\"}",
                        "{\"sensorId\":\"SENS900\",\"unidadeId\":\"UNI001\",\"tipoMedida\":\"energia_kwh\",\"valor\":101.5,\"timestamp\":\"2026-08-01T11:00:00Z\"}"),
                new Recurso("alertas", "/api/alertas", "alerta.schema.json", "ALT900",
                        "{\"id\":\"ALT900\",\"sensorId\":\"SENS900\",\"unidadeId\":\"UNI001\",\"tipoAlerta\":\"consumo_energia_excedido\",\"severidade\":\"alta\",\"status\":\"aberto\",\"atributos\":{\"valor_limite\":7.5}}",
                        "{\"sensorId\":\"SENS900\",\"unidadeId\":\"UNI001\",\"tipoAlerta\":\"consumo_energia_excedido\",\"severidade\":\"alta\",\"status\":\"resolvido\",\"atributos\":{}}"),
                new Recurso("unidades", "/api/unidades", "unidade.schema.json", "UNI900",
                        "{\"id\":\"UNI900\",\"nome\":\"Escola Municipal\",\"tipo\":\"publico\",\"atributos\":{\"num_alunos\":620}}",
                        "{\"nome\":\"Escola Municipal Porto Verde\",\"tipo\":\"publico\",\"atributos\":{\"num_alunos\":640}}"),
                new Recurso("conformidade", "/api/conformidade", "conformidade.schema.json", "GOV900",
                        "{\"id\":\"GOV900\",\"unidadeId\":\"UNI900\",\"tipo\":\"licenca_ambiental\",\"status\":\"vencida\",\"atributos\":{\"orgao_responsavel\":\"CETESB\"}}",
                        "{\"unidadeId\":\"UNI900\",\"tipo\":\"licenca_ambiental\",\"status\":\"vigente\",\"atributos\":{\"orgao_responsavel\":\"CETESB\"}}"));
    }

    private ApiHarness api;

    @BeforeEach
    void setUp() {
        api = new ApiHarness();
    }

    @ParameterizedTest
    @MethodSource("recursos")
    void listarSemDadosRetorna200EListaVazia(Recurso r) throws Exception {
        MockHttpServletResponse resposta = api.get(r.caminho());

        assertEquals(200, resposta.getStatus());
        JsonNode corpo = api.json(resposta);
        assertTrue(corpo.isArray());
        assertEquals(0, corpo.size());
    }

    @ParameterizedTest
    @MethodSource("recursos")
    void criarRetorna201ComJsonEContratoValido(Recurso r) throws Exception {
        MockHttpServletResponse resposta = api.post(r.caminho(), r.corpo());

        assertEquals(201, resposta.getStatus());
        assertTrue(resposta.getContentType().startsWith("application/json"));
        JsonNode corpo = api.json(resposta);
        assertEquals(r.id(), corpo.get("id").asText());
        JsonContract.assertValido(r.schema(), corpo);
    }

    @ParameterizedTest
    @MethodSource("recursos")
    void criarSemIdGeraIdentificador(Recurso r) throws Exception {
        ObjectNode semId = (ObjectNode) api.mapper().readTree(r.corpo());
        semId.remove("id");

        MockHttpServletResponse resposta = api.post(r.caminho(), semId.toString());

        assertEquals(201, resposta.getStatus());
        JsonNode corpo = api.json(resposta);
        assertNotNull(corpo.get("id"));
        assertFalse(corpo.get("id").asText().isBlank());
        JsonContract.assertValido(r.schema(), corpo);
    }

    @ParameterizedTest
    @MethodSource("recursos")
    void buscarPorIdRetorna200ComContratoValido(Recurso r) throws Exception {
        api.post(r.caminho(), r.corpo());

        MockHttpServletResponse resposta = api.get(r.caminho() + "/" + r.id());

        assertEquals(200, resposta.getStatus());
        JsonNode corpo = api.json(resposta);
        assertEquals(r.id(), corpo.get("id").asText());
        JsonContract.assertValido(r.schema(), corpo);
    }

    @ParameterizedTest
    @MethodSource("recursos")
    void listarComDadosRetornaItensComContratoValido(Recurso r) throws Exception {
        api.post(r.caminho(), r.corpo());

        MockHttpServletResponse resposta = api.get(r.caminho());

        assertEquals(200, resposta.getStatus());
        JsonNode corpo = api.json(resposta);
        assertEquals(1, corpo.size());
        JsonContract.assertTodosValidos(r.schema(), corpo);
    }

    @ParameterizedTest
    @MethodSource("recursos")
    void atualizarRetorna200EPersisteAMudanca(Recurso r) throws Exception {
        api.post(r.caminho(), r.corpo());

        MockHttpServletResponse atualizado = api.put(r.caminho() + "/" + r.id(), r.corpoAtualizado());

        assertEquals(200, atualizado.getStatus());
        JsonNode corpoPut = api.json(atualizado);
        assertEquals(r.id(), corpoPut.get("id").asText());
        JsonContract.assertValido(r.schema(), corpoPut);
        JsonNode corpoGet = api.json(api.get(r.caminho() + "/" + r.id()));
        assertEquals(corpoPut, corpoGet);
    }

    @ParameterizedTest
    @MethodSource("recursos")
    void atualizarInexistenteRetorna404(Recurso r) throws Exception {
        assertEquals(404, api.put(r.caminho() + "/NAO_EXISTE", r.corpoAtualizado()).getStatus());
    }

    @ParameterizedTest
    @MethodSource("recursos")
    void excluirRetorna204EDepoisABuscaRetorna404(Recurso r) throws Exception {
        api.post(r.caminho(), r.corpo());

        assertEquals(204, api.delete(r.caminho() + "/" + r.id()).getStatus());
        assertEquals(404, api.get(r.caminho() + "/" + r.id()).getStatus());
    }

    @ParameterizedTest
    @MethodSource("recursos")
    void buscarEExcluirInexistentesRetornam404(Recurso r) throws Exception {
        assertEquals(404, api.get(r.caminho() + "/NAO_EXISTE").getStatus());
        assertEquals(404, api.delete(r.caminho() + "/NAO_EXISTE").getStatus());
    }

    @ParameterizedTest
    @MethodSource("recursos")
    void criarComJsonMalformadoRetorna400(Recurso r) throws Exception {
        assertEquals(400, api.post(r.caminho(), "{\"tipo\": ").getStatus());
    }
}
