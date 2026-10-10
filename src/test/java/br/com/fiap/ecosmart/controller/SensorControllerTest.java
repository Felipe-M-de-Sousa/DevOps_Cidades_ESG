package br.com.fiap.ecosmart.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.ecosmart.model.Sensor;
import br.com.fiap.ecosmart.repository.SensorRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class SensorControllerTest {

    private SensorRepository repository;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        repository = mock(SensorRepository.class);
        mvc = MockMvcBuilders.standaloneSetup(new SensorController(repository)).build();
    }

    private Sensor sensor(String id) {
        Sensor s = new Sensor();
        s.setId(id);
        s.setTipo("medidor_energia");
        s.setStatus("ativo");
        return s;
    }

    @Test
    void listarRetornaSensores() throws Exception {
        when(repository.findAll()).thenReturn(List.of(sensor("SENS001")));

        mvc.perform(get("/api/sensores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("SENS001"))
                .andExpect(jsonPath("$[0].tipo").value("medidor_energia"));
    }

    @Test
    void buscarInexistenteRetorna404() throws Exception {
        when(repository.findById("X")).thenReturn(Optional.empty());

        mvc.perform(get("/api/sensores/X")).andExpect(status().isNotFound());
    }

    @Test
    void criarRetorna201() throws Exception {
        when(repository.save(any(Sensor.class))).thenAnswer(inv -> inv.getArgument(0));

        mvc.perform(post("/api/sensores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"SENS099\",\"tipo\":\"vazao_agua\",\"status\":\"ativo\","
                                + "\"atributos\":{\"vazao_lmin\":12.3}}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("SENS099"))
                .andExpect(jsonPath("$.atributos.vazao_lmin").value(12.3));
    }

    @Test
    void excluirExistenteRetorna204() throws Exception {
        when(repository.existsById("SENS001")).thenReturn(true);

        mvc.perform(delete("/api/sensores/SENS001")).andExpect(status().isNoContent());
        verify(repository).deleteById("SENS001");
    }

    @Test
    void excluirInexistenteRetorna404() throws Exception {
        when(repository.existsById("X")).thenReturn(false);

        mvc.perform(delete("/api/sensores/X")).andExpect(status().isNotFound());
    }
}
