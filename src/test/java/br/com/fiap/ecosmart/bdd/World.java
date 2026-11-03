package br.com.fiap.ecosmart.bdd;

import br.com.fiap.ecosmart.support.ApiHarness;
import org.springframework.mock.web.MockHttpServletResponse;

/** Estado compartilhado entre os steps de um cenario (uma instancia nova por cenario). */
public class World {

    private final ApiHarness harness = new ApiHarness();
    private MockHttpServletResponse resposta;

    public ApiHarness harness() {
        return harness;
    }

    public MockHttpServletResponse resposta() {
        return resposta;
    }

    public void setResposta(MockHttpServletResponse resposta) {
        this.resposta = resposta;
    }
}
