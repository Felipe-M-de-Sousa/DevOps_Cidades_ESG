package br.com.fiap.ecosmart.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Set;

/** Teste de contrato: valida um JSON contra um arquivo JSON Schema em src/test/resources/schemas. */
public final class JsonContract {

    private static final JsonSchemaFactory FACTORY =
            JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    private JsonContract() {
    }

    public static Set<ValidationMessage> validar(String arquivoSchema, JsonNode json) {
        try (InputStream in = JsonContract.class.getResourceAsStream("/schemas/" + arquivoSchema)) {
            if (in == null) {
                throw new IllegalArgumentException("Schema nao encontrado: " + arquivoSchema);
            }
            JsonSchema schema = FACTORY.getSchema(in);
            return schema.validate(json);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void assertValido(String arquivoSchema, JsonNode json) {
        Set<ValidationMessage> erros = validar(arquivoSchema, json);
        assertTrue(erros.isEmpty(), () -> "Contrato '" + arquivoSchema + "' violado: " + erros);
    }

    public static void assertTodosValidos(String arquivoSchema, JsonNode lista) {
        assertTrue(lista.isArray(), "Era esperada uma lista JSON, mas veio: " + lista);
        for (JsonNode item : lista) {
            assertValido(arquivoSchema, item);
        }
    }
}
