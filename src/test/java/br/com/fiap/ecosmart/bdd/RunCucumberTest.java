package br.com.fiap.ecosmart.bdd;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Executa todos os cenarios Gherkin de src/test/resources/features.
 * Filtrar por tag: mvn test -Dcucumber.filter.tags="@negativo"
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.glue", value = "br.com.fiap.ecosmart.bdd")
public class RunCucumberTest {
}
