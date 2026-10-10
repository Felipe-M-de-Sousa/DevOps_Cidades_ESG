package br.com.fiap.ecosmart.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.ecosmart.model.Alerta;
import br.com.fiap.ecosmart.repository.AlertaRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AlertaControllerTest {

    private AlertaRepository repository;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        repository = mock(AlertaRepository.class);
        mvc = MockMvcBuilders.standaloneSetup(new AlertaController(repository)).build();
    }

    @Test
    void abertosRetornaApenasAlertasAbertos() throws Exception {
        Alerta a = new Alerta();
        a.setId("ALT002");
        a.setStatus("aberto");
        a.setSeveridade("alta");
        when(repository.findByStatus("aberto")).thenReturn(List.of(a));

        mvc.perform(get("/api/alertas/abertos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].severidade").value("alta"));
    }
}
