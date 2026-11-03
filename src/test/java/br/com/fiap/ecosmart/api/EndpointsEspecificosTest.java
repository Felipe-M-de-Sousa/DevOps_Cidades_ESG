package br.com.fiap.ecosmart.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.fiap.ecosmart.support.ApiHarness;
import br.com.fiap.ecosmart.support.JsonContract;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

/** Testes de API dos endpoints que nao fazem parte do CRUD generico. */
class EndpointsEspecificosTest {

    private ApiHarness api;

    @BeforeEach
    void setUp() {
        api = new ApiHarness();
    }

    @Test
    void ambienteRetorna200ComContratoValido() throws Exception {
        MockHttpServletResponse resposta = api.get("/api/ambiente");

        assertEquals(200, resposta.getStatus());
        assertTrue(resposta.getContentType().startsWith("application/json"));
        JsonNode corpo = api.json(resposta);
        JsonContract.assertValido("ambiente.schema.json", corpo);
        assertEquals(ApiHarness.AMBIENTE_DE_TESTE, corpo.get("ambiente").asText());
    }

    @Test
    void alertasAbertosRetornaSomenteOsAbertos() throws Exception {
        api.post("/api/alertas", "{\"id\":\"A1\",\"severidade\":\"alta\",\"status\":\"aberto\",\"atributos\":{}}");
        api.post("/api/alertas", "{\"id\":\"A2\",\"severidade\":\"baixa\",\"status\":\"resolvido\",\"atributos\":{}}");
        api.post("/api/alertas", "{\"id\":\"A3\",\"severidade\":\"critica\",\"status\":\"aberto\",\"atributos\":{}}");

        MockHttpServletResponse resposta = api.get("/api/alertas/abertos");

        assertEquals(200, resposta.getStatus());
        JsonNode corpo = api.json(resposta);
        assertEquals(2, corpo.size());
        JsonContract.assertTodosValidos("alerta.schema.json", corpo);
        for (JsonNode alerta : corpo) {
            assertEquals("aberto", alerta.get("status").asText());
        }
    }

    @Test
    void alertasAbertosSemAlertasRetornaListaVazia() throws Exception {
        MockHttpServletResponse resposta = api.get("/api/alertas/abertos");

        assertEquals(200, resposta.getStatus());
        JsonNode corpo = api.json(resposta);
        assertTrue(corpo.isArray());
        assertEquals(0, corpo.size());
    }
}
