package br.com.fiap.ecosmart.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.fiap.ecosmart.support.JsonContract;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/** Garante que os proprios JSON Schemas funcionam: aceitam o contrato correto e rejeitam desvios. */
class JsonContractTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private JsonNode json(String texto) throws Exception {
        return mapper.readTree(texto);
    }

    @Test
    void sensorValidoEAceito() throws Exception {
        JsonNode sensor = json("{\"id\":\"SENS1\",\"tipo\":\"medidor_energia\",\"status\":\"ativo\",\"atributos\":{\"potencia_kw\":4.01}}");

        assertTrue(JsonContract.validar("sensor.schema.json", sensor).isEmpty());
    }

    @Test
    void sensorSemIdViolaOContrato() throws Exception {
        JsonNode semId = json("{\"tipo\":\"medidor_energia\",\"atributos\":{}}");

        assertFalse(JsonContract.validar("sensor.schema.json", semId).isEmpty());
    }

    @Test
    void campoComTipoErradoViolaOContrato() throws Exception {
        JsonNode valorTexto = json("{\"id\":\"L1\",\"valor\":\"alto\",\"atributos\":{}}");

        assertFalse(JsonContract.validar("leitura.schema.json", valorTexto).isEmpty());
    }

    @Test
    void campoNaoPrevistoViolaOContrato() throws Exception {
        JsonNode extra = json("{\"id\":\"U1\",\"nome\":\"Escola\",\"campoInventado\":1,\"atributos\":{}}");

        assertFalse(JsonContract.validar("unidade.schema.json", extra).isEmpty());
    }

    @Test
    void ambienteComValorForaDaListaViolaOContrato() throws Exception {
        JsonNode invalido = json("{\"aplicacao\":\"EcoSmart\",\"ambiente\":\"homologacao\",\"versao\":\"1.0.0\"}");

        assertFalse(JsonContract.validar("ambiente.schema.json", invalido).isEmpty());
    }
}
