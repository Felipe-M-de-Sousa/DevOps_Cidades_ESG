package br.com.fiap.ecosmart.support;

import br.com.fiap.ecosmart.controller.AlertaController;
import br.com.fiap.ecosmart.controller.AmbienteController;
import br.com.fiap.ecosmart.controller.ConformidadeController;
import br.com.fiap.ecosmart.controller.LeituraController;
import br.com.fiap.ecosmart.controller.SensorController;
import br.com.fiap.ecosmart.controller.UnidadeController;
import br.com.fiap.ecosmart.repository.AlertaRepository;
import br.com.fiap.ecosmart.repository.ConformidadeRepository;
import br.com.fiap.ecosmart.repository.LeituraRepository;
import br.com.fiap.ecosmart.repository.SensorRepository;
import br.com.fiap.ecosmart.repository.UnidadeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Sobe todos os controllers da API (com repositorios em memoria) e expoe
 * metodos simples para chamar os endpoints HTTP nos testes.
 */
public class ApiHarness {

    public static final String AMBIENTE_DE_TESTE = "staging";

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json()
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
    private final MockMvc mvc;

    public ApiHarness() {
        mvc = MockMvcBuilders.standaloneSetup(
                        new SensorController(InMemoryRepository.of(SensorRepository.class)),
                        new LeituraController(InMemoryRepository.of(LeituraRepository.class)),
                        new AlertaController(InMemoryRepository.of(AlertaRepository.class)),
                        new UnidadeController(InMemoryRepository.of(UnidadeRepository.class)),
                        new ConformidadeController(InMemoryRepository.of(ConformidadeRepository.class)),
                        new AmbienteController(AMBIENTE_DE_TESTE))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();
    }

    public MockHttpServletResponse get(String path) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get(path)).andReturn().getResponse();
    }

    public MockHttpServletResponse post(String path, String corpo) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON).content(corpo)).andReturn().getResponse();
    }

    public MockHttpServletResponse put(String path, String corpo) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.put(path)
                .contentType(MediaType.APPLICATION_JSON).content(corpo)).andReturn().getResponse();
    }

    public MockHttpServletResponse delete(String path) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.delete(path)).andReturn().getResponse();
    }

    public JsonNode json(MockHttpServletResponse resposta) throws Exception {
        return mapper.readTree(resposta.getContentAsString(StandardCharsets.UTF_8));
    }

    public ObjectMapper mapper() {
        return mapper;
    }
}
