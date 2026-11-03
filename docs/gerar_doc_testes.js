const { Document, Packer, Paragraph, TextRun, HeadingLevel, AlignmentType, Table, TableRow, TableCell,
  WidthType, ShadingType, BorderStyle, Footer, PageNumber, PageBreak, LevelFormat } = require("docx");
const fs = require("fs");
const F = "Calibri", C = "Consolas", G = "1E5631";
const cen = JSON.parse(fs.readFileSync("/tmp/cenarios.json", "utf-8"));
const h1 = t => new Paragraph({ heading: HeadingLevel.HEADING_1, spacing: { before: 360, after: 160 }, children: [new TextRun({ text: t, bold: true, color: G, font: F })] });
const h2 = t => new Paragraph({ heading: HeadingLevel.HEADING_2, spacing: { before: 240, after: 120 }, children: [new TextRun({ text: t, bold: true, font: F })] });
const p = t => new Paragraph({ spacing: { after: 140, line: 290 }, alignment: AlignmentType.JUSTIFIED, children: [new TextRun({ text: t, font: F, size: 22 })] });
const bl = t => new Paragraph({ numbering: { reference: "b", level: 0 }, spacing: { after: 70 }, children: [new TextRun({ text: t, font: F, size: 22 })] });
const gap = () => new Paragraph({ spacing: { after: 100 }, children: [] });
const code = lines => new Table({ width: { size: 9400, type: WidthType.DXA }, columnWidths: [9400],
  rows: [new TableRow({ children: [new TableCell({ width: { size: 9400, type: WidthType.DXA }, shading: { type: ShadingType.CLEAR, fill: "1B1B1B" },
    margins: { top: 120, bottom: 120, left: 160, right: 160 },
    children: lines.map(l => new Paragraph({ spacing: { after: 10 }, children: [new TextRun({ text: l || " ", font: C, size: 17, color: "D4F4D4" })] })) })] })] });
const print = t => new Table({ width: { size: 9400, type: WidthType.DXA }, columnWidths: [9400],
  rows: [new TableRow({ height: { value: 2400, rule: "atLeast" }, children: [new TableCell({ width: { size: 9400, type: WidthType.DXA },
    borders: ["top","bottom","left","right"].reduce((o,k)=>(o[k]={style:BorderStyle.DASHED,size:6,color:"888888"},o),{}),
    margins: { top: 100, bottom: 100, left: 160, right: 160 },
    children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "[INSERIR PRINT] " + t, italics: true, color: "777777", font: F, size: 20 })] })] })] })] });
const tbl = (head, rows, w) => new Table({ width: { size: w.reduce((a, b) => a + b), type: WidthType.DXA }, columnWidths: w,
  rows: [new TableRow({ tableHeader: true, children: head.map((t, i) => new TableCell({ width: { size: w[i], type: WidthType.DXA }, shading: { type: ShadingType.CLEAR, fill: G }, margins: { top: 70, bottom: 70, left: 100, right: 100 }, children: [new Paragraph({ children: [new TextRun({ text: t, bold: true, color: "FFFFFF", font: F, size: 18 })] })] })) }),
    ...rows.map(r => new TableRow({ children: r.map((t, i) => new TableCell({ width: { size: w[i], type: WidthType.DXA }, margins: { top: 60, bottom: 60, left: 100, right: 100 }, children: [new Paragraph({ children: [new TextRun({ text: String(t), font: F, size: 18 })] })] })) }))] });

const pilar = tags => tags.filter(t => ["@ambiental","@social","@governanca"].includes(t)).map(t => ({"@ambiental":"Ambiental","@social":"Social","@governanca":"Governança"}[t])).join(" + ");
const tipo = tags => tags.includes("@negativo") ? "Negativo" : "Positivo";
const totalExec = cen.reduce((a, c) => a + c.execucoes, 0);
const pos = cen.filter(c => c.tags.includes("@positivo")).reduce((a, c) => a + c.execucoes, 0);
const neg = totalExec - pos;

const doc = new Document({
  numbering: { config: [{ reference: "b", levels: [{ level: 0, format: LevelFormat.BULLET, text: "•", alignment: AlignmentType.LEFT, style: { paragraph: { indent: { left: 540, hanging: 270 } } } }] }] },
  sections: [{
    properties: { page: { size: { width: 11906, height: 16838 }, margin: { top: 1300, bottom: 1300, left: 1250, right: 1250 } } },
    footers: { default: new Footer({ children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "Cidades ESG Inteligentes — Testes Automatizados  |  Página ", size: 18, font: F }), new TextRun({ children: [PageNumber.CURRENT], size: 18, font: F })] })] }) },
    children: [
      new Paragraph({ spacing: { before: 2200 }, alignment: AlignmentType.CENTER, children: [new TextRun({ text: "FIAP ON — Testes Automatizados", bold: true, size: 26, color: G, font: F })] }),
      new Paragraph({ spacing: { before: 300, after: 200 }, alignment: AlignmentType.CENTER, children: [new TextRun({ text: "Projeto Cidades ESG Inteligentes", bold: true, size: 44, font: F })] }),
      new Paragraph({ spacing: { after: 1200 }, alignment: AlignmentType.CENTER, children: [new TextRun({ text: "EcoSmart Porto Verde — BDD com Cucumber/Gherkin, testes de API e testes de contrato (JSON Schema)", italics: true, size: 26, font: F })] }),
      new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 80 }, children: [new TextRun({ text: "Integrantes:", bold: true, size: 24, font: F })] }),
      new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "[Nome completo e RM de cada integrante — preencher]", italics: true, size: 22, font: F })] }),
      new Paragraph({ spacing: { before: 400 }, alignment: AlignmentType.CENTER, children: [new TextRun({ text: "Repositório: [link]   |   Pipeline: [link]", size: 22, font: F })] }),
      new Paragraph({ children: [new PageBreak()] }),

      h1("1. Objetivo e ligação com ESG"),
      p("Este trabalho aplica automação de testes à API EcoSmart Porto Verde (Java 21, Spring Boot, MongoDB), desenvolvida nas atividades anteriores, para validar as funcionalidades críticas e garantir que a aplicação entregue o que promete nos três pilares ESG."),
      tbl(["Pilar", "O que os testes garantem"], [
        ["Ambiental", "Sensores IoT de tipos diferentes são aceitos com atributos próprios (energia, água, qualidade do ar); leituras de consumo são registradas e validadas; alertas de consumo excedido e vazamento ficam consultáveis e são resolvidos corretamente."],
        ["Social", "Indicadores de diversidade (mulheres em liderança, PCD) e de acessibilidade (rampa, elevador) são gravados e atualizados com a estrutura aninhada correta, base para acompanhar metas de inclusão."],
        ["Governança", "Licenças ambientais, relatórios de carbono e auditorias são registrados, renovados e rastreáveis; erros (404 e 400) são tratados de forma previsível; o ambiente em execução é identificável (/api/ambiente), dando rastreabilidade do que está no ar."],
      ], [1800, 7600]),
      gap(),
      p("Qualidade de software também é um tema de governança: testes automatizados executados a cada commit reduzem retrabalho e falhas em produção, o que evita desperdício de recursos computacionais e de tempo da equipe."),

      h1("2. Estratégia de testes"),
      tbl(["Camada", "Ferramenta", "Quantidade", "Foco"], [
        ["BDD", "Cucumber 7 + Gherkin (pt)", totalExec + " execuções de cenário", "Comportamento do ponto de vista do usuário; caminhos felizes e de falha"],
        ["API (CRUD)", "JUnit 5 + MockMvc", "50 (10 testes × 5 recursos)", "Status code e corpo JSON de GET, POST, PUT e DELETE nas 5 collections"],
        ["API (específicos)", "JUnit 5 + MockMvc", "3", "/api/alertas/abertos e /api/ambiente"],
        ["Contrato", "JSON Schema (draft-07) + networknt", "6 schemas, 5 testes do próprio contrato", "Estrutura, tipos e ausência de campos extras nas respostas"],
        ["Unitários", "JUnit 5 + Mockito", "7", "Controllers isolados"],
      ], [1500, 2500, 2000, 3400]),
      gap(),
      h2("Decisões de projeto"),
      bl("Repositórios em memória (via proxy): os testes não dependem de MongoDB nem de Docker, rodam em segundos e são determinísticos, na máquina do aluno e no GitHub Actions."),
      bl("As requisições passam pela camada HTTP real do Spring (MockMvc standalone com os controllers e o serializador JSON da aplicação), então status code, cabeçalhos e JSON são os mesmos que o cliente recebe."),
      bl("Os passos Gherkin são genéricos (enviar GET/POST/PUT/DELETE, verificar status, campo e contrato), o que permite escrever novos cenários só com arquivos .feature."),
      bl("Tags (@positivo, @negativo, @ambiental, @social, @governanca) permitem executar subconjuntos, por exemplo só os cenários negativos ou só o pilar Social."),
      bl("A integração do banco real (MongoDB) é validada no deploy, pelos smoke tests do pipeline (health check, /api/ambiente e /api/sensores)."),

      h1("3. Cenários BDD (Gherkin)"),
      p(`Foram escritos ${cen.length} cenários em 6 arquivos .feature (${totalExec} execuções, pois um Esquema do Cenário roda uma vez por linha de exemplos): ${pos} positivos e ${neg} negativos. O mínimo exigido é 3.`),
      tbl(["Feature", "Cenário", "Tipo", "Pilar"], cen.map(c => [c.feature.replace(".feature", ""), c.cenario + (c.execucoes > 1 ? ` (${c.execucoes} exemplos)` : ""), tipo(c.tags), pilar(c.tags)]), [1500, 5000, 1000, 1900]),
      gap(),
      h2("Exemplo 1 — cenário positivo (pilar Ambiental)"),
      code(["# language: pt", "@positivo", "Cenário: Cadastrar um medidor de energia com sucesso", "  Quando eu envio POST para \"/api/sensores\" com o corpo:", "    \"\"\"", "    {\"id\":\"SENS100\",\"tipo\":\"medidor_energia\",\"status\":\"ativo\",", "     \"atributos\":{\"potencia_kw\":4.01,\"limite_alerta_kw\":8.0}}", "    \"\"\"", "  Então o status da resposta deve ser 201", "  E o corpo deve respeitar o contrato \"sensor.schema.json\"", "  E o corpo deve ter o campo \"atributos.potencia_kw\" igual a \"4.01\""]),
      gap(),
      h2("Exemplo 2 — cenário negativo (pilar Governança)"),
      code(["@negativo", "Cenário: Resolver um alerta que não existe", "  Quando eu envio PUT para \"/api/alertas/NAO_EXISTE\" com o corpo:", "    \"\"\"", "    {\"status\":\"resolvido\",\"atributos\":{}}", "    \"\"\"", "  Então o status da resposta deve ser 404"]),
      gap(),
      h2("Exemplo 3 — esquema de cenário (schema flexível do MongoDB)"),
      code(["Esquema do Cenário: Sensores de tipos diferentes guardam atributos", "                     diferentes na mesma collection", "  Quando eu envio POST para \"/api/sensores\" com o corpo:", "    \"\"\"", "    {\"id\":\"<id>\",\"tipo\":\"<tipo>\",\"status\":\"ativo\",\"atributos\":{\"<atributo>\":<valor>}}", "    \"\"\"", "  Então o status da resposta deve ser 201", "  E o corpo deve respeitar o contrato \"sensor.schema.json\"", "", "  Exemplos:", "    | id      | tipo            | atributo           | valor |", "    | SENS201 | medidor_energia | potencia_kw        | 6.74  |", "    | SENS202 | qualidade_ar    | pm25_ugm3          | 41.2  |", "    | SENS203 | vazao_agua      | vazao_lmin         | 55.0  |", "    | SENS204 | ocupacao        | pessoas_detectadas | 3     |"]),

      h1("4. Testes de API — todas as APIs"),
      tbl(["Endpoint", "Métodos testados", "Status validados", "Contrato"], [
        ["/api/sensores", "GET, GET/{id}, POST, PUT/{id}, DELETE/{id}", "200, 201, 204, 400, 404", "sensor.schema.json"],
        ["/api/leituras", "GET, GET/{id}, POST, PUT/{id}, DELETE/{id}", "200, 201, 204, 400, 404", "leitura.schema.json"],
        ["/api/alertas", "GET, GET/{id}, POST, PUT/{id}, DELETE/{id}", "200, 201, 204, 400, 404", "alerta.schema.json"],
        ["/api/alertas/abertos", "GET", "200 (com e sem alertas abertos)", "alerta.schema.json"],
        ["/api/unidades", "GET, GET/{id}, POST, PUT/{id}, DELETE/{id}", "200, 201, 204, 400, 404", "unidade.schema.json"],
        ["/api/conformidade", "GET, GET/{id}, POST, PUT/{id}, DELETE/{id}", "200, 201, 204, 400, 404", "conformidade.schema.json"],
        ["/api/ambiente", "GET", "200", "ambiente.schema.json"],
      ], [2100, 3100, 2200, 2000]),
      gap(),
      p("Em cada recurso do CRUD, os testes verificam: lista vazia (200); criação com id informado e sem id, com geração automática (201); busca por id (200); listagem com dados (200); atualização com persistência da mudança (200); atualização, busca e exclusão de registro inexistente (404); exclusão (204) seguida de busca (404); e JSON malformado (400). Em todas as respostas de sucesso, o tipo de conteúdo é application/json e o corpo é validado contra o JSON Schema."),

      h1("5. Testes de contrato (JSON Schema)"),
      p("Cada resposta é validada contra um schema draft-07 em src/test/resources/schemas. Os schemas exigem id e atributos, definem o tipo de cada campo, aceitam null nos campos opcionais e proíbem campos não previstos (additionalProperties: false), de modo que qualquer mudança não combinada no formato da resposta quebra o teste."),
      tbl(["Schema", "Regras principais"], [
        ["sensor.schema.json", "id (string não vazia) e atributos (objeto) obrigatórios; tipo, unidadeId e status string ou null"],
        ["leitura.schema.json", "valor numérico (ou null); timestamp em texto ISO-8601 (ou null)"],
        ["alerta.schema.json", "tipoAlerta, severidade e status string ou null"],
        ["unidade.schema.json", "nome e tipo string ou null; atributos aninhados livres (diversidade, acessibilidade)"],
        ["conformidade.schema.json", "unidadeId, tipo e status string ou null"],
        ["ambiente.schema.json", "ambiente restrito a local, staging ou production; versao no padrão X.Y.Z"],
      ], [2600, 6800]),
      gap(),
      p("A classe JsonContractTest garante que os próprios contratos funcionam: aceita um sensor válido e rejeita sensor sem id, campo com tipo errado (valor texto numa leitura), campo inventado e ambiente fora da lista."),

      h1("6. Como executar"),
      h2("Localmente (Java 21 e Maven)"),
      code(["mvn -B verify                                   # tudo", "mvn test -Dtest=RunCucumberTest                 # somente BDD", "mvn test -Dtest=ApiContratoTest                 # somente API e contrato", "mvn test -Dtest=RunCucumberTest -Dcucumber.filter.tags=\"@negativo\"", "", "# relatorio HTML do Cucumber:", "#   target/cucumber-reports/cucumber.html"]),
      h2("No pipeline (GitHub Actions)"),
      p("O job \"Build e testes (Maven)\" do arquivo .github/workflows/ci-cd.yml executa mvn -B verify a cada push e pull request. Se qualquer teste falhar, o pipeline para antes de gerar a imagem Docker e de fazer deploy. Os relatórios do JUnit e do Cucumber ficam disponíveis como artefato \"relatorio-testes\"."),

      h1("7. Evidências de execução"),
      print("Terminal: saída do 'mvn -B verify' com 'Tests run: ..., Failures: 0, Errors: 0' e BUILD SUCCESS"),
      gap(),
      print("Relatório HTML do Cucumber (target/cucumber-reports/cucumber.html) com todos os cenários passando"),
      gap(),
      print("GitHub Actions: job 'Build e testes (Maven)' verde e artefato 'relatorio-testes'"),
      gap(),
      print("(Opcional) Execução filtrada por tag, ex.: -Dcucumber.filter.tags=\"@negativo\""),
    ],
  }],
});
Packer.toBuffer(doc).then(b => { fs.writeFileSync("Documentacao_Testes_Automatizados.docx", b); console.log("ok"); });
