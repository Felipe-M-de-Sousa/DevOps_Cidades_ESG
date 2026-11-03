package br.com.fiap.ecosmart.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.fiap.ecosmart.support.JsonContract;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.mock.web.MockHttpServletResponse;

/** Steps genericos reutilizados por todas as features (a palavra-chave Given/When/Then nao importa). */
public class ApiSteps {

    private final World world;

    public ApiSteps(World world) {
        this.world = world;
    }

    // ---------------------------------------------------------------- Dado
    @Given("que existe em {string} o registro:")
    public void queExisteORegistro(String caminho, String corpo) throws Exception {
        MockHttpServletResponse r = world.harness().post(caminho, corpo);
        assertEquals(201, r.getStatus(), "A pre-condicao falhou ao cadastrar: " + r.getContentAsString());
    }

    // -------------------------------------------------------------- Quando
    @When("eu envio POST para {string} com o corpo:")
    public void euEnvioPost(String caminho, String corpo) throws Exception {
        world.setResposta(world.harness().post(caminho, corpo));
    }

    @When("eu envio PUT para {string} com o corpo:")
    public void euEnvioPut(String caminho, String corpo) throws Exception {
        world.setResposta(world.harness().put(caminho, corpo));
    }

    @When("eu envio GET para {string}")
    public void euEnvioGet(String caminho) throws Exception {
        world.setResposta(world.harness().get(caminho));
    }

    @When("eu envio DELETE para {string}")
    public void euEnvioDelete(String caminho) throws Exception {
        world.setResposta(world.harness().delete(caminho));
    }

    // --------------------------------------------------------------- Entao
    @Then("o status da resposta deve ser {int}")
    public void oStatusDeveSer(int esperado) throws Exception {
        assertNotNull(world.resposta(), "Nenhuma requisicao foi feita antes desta verificacao");
        assertEquals(esperado, world.resposta().getStatus(),
                "Corpo recebido: " + world.resposta().getContentAsString());
    }

    @Then("o corpo deve respeitar o contrato {string}")
    public void oCorpoDeveRespeitarOContrato(String schema) throws Exception {
        JsonContract.assertValido(schema, corpo());
    }

    @Then("todos os itens da lista devem respeitar o contrato {string}")
    public void todosOsItensDevemRespeitarOContrato(String schema) throws Exception {
        JsonContract.assertTodosValidos(schema, corpo());
    }

    @Then("o corpo deve ser uma lista com {int} item/itens")
    public void oCorpoDeveSerUmaListaCom(int quantidade) throws Exception {
        JsonNode corpo = corpo();
        assertTrue(corpo.isArray(), "Era esperada uma lista, mas veio: " + corpo);
        assertEquals(quantidade, corpo.size());
    }

    @Then("o corpo deve ter o campo {string} igual a {string}")
    public void oCorpoDeveTerOCampoIgualA(String campo, String esperado) throws Exception {
        JsonNode valor = campo(campo);
        assertEquals(esperado, valor.asText(), "Campo '" + campo + "'");
    }

    @Then("o corpo deve conter o campo {string}")
    public void oCorpoDeveConterOCampo(String campo) throws Exception {
        JsonNode valor = campo(campo);
        assertFalse(valor.isNull() || valor.asText().isBlank(), "Campo '" + campo + "' esta vazio");
    }

    // ------------------------------------------------------------- helpers
    private JsonNode corpo() throws Exception {
        return world.harness().json(world.resposta());
    }

    /** Aceita caminhos como "atributos.potencia_kw" ou "0.status" (indice de lista). */
    private JsonNode campo(String caminho) throws Exception {
        JsonNode valor = corpo().at("/" + caminho.replace('.', '/'));
        assertFalse(valor.isMissingNode(), "Campo '" + caminho + "' nao existe em: " + corpo());
        return valor;
    }
}
