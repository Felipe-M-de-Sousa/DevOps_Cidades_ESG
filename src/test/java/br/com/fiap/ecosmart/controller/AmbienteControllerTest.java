package br.com.fiap.ecosmart.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AmbienteControllerTest {

    @Test
    void informaAmbienteConfigurado() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new AmbienteController("staging")).build();

        mvc.perform(get("/api/ambiente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ambiente").value("staging"))
                .andExpect(jsonPath("$.versao").value("1.0.0"));
    }
}
