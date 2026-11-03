# language: pt
@governanca
Funcionalidade: Conformidade e rastreabilidade ambiental
  Para garantir integridade e conformidade regulatória, quero registrar licenças,
  auditorias e relatórios de carbono e manter o histórico de cada registro.

  @positivo
  Cenário: Registrar uma licença ambiental vigente
    Quando eu envio POST para "/api/conformidade" com o corpo:
      """
      {"id":"GOV100","unidadeId":"UNI008","tipo":"licenca_ambiental","status":"vigente","atributos":{"orgao_responsavel":"CETESB","numero_licenca":"LA-2026-4471","data_validade":"2027-06-01"}}
      """
    Então o status da resposta deve ser 201
    E o corpo deve respeitar o contrato "conformidade.schema.json"
    E o corpo deve ter o campo "status" igual a "vigente"
    E o corpo deve ter o campo "atributos.numero_licenca" igual a "LA-2026-4471"

  @positivo
  Cenário: Registrar um relatório de emissão de carbono com compensação
    Quando eu envio POST para "/api/conformidade" com o corpo:
      """
      {"id":"GOV101","unidadeId":"UNI010","tipo":"relatorio_emissao_carbono","atributos":{"periodo_referencia":"2026-T2","emissoes_co2_toneladas":3.1,"compensacao_realizada":true}}
      """
    Então o status da resposta deve ser 201
    E o corpo deve ter o campo "atributos.emissoes_co2_toneladas" igual a "3.1"
    E o corpo deve ter o campo "atributos.compensacao_realizada" igual a "true"

  @positivo
  Cenário: Renovar uma licença vencida
    Dado que existe em "/api/conformidade" o registro:
      """
      {"id":"GOV102","unidadeId":"UNI005","tipo":"licenca_ambiental","status":"vencida","atributos":{"alerta_renovacao":true}}
      """
    Quando eu envio PUT para "/api/conformidade/GOV102" com o corpo:
      """
      {"unidadeId":"UNI005","tipo":"licenca_ambiental","status":"vigente","atributos":{"alerta_renovacao":false}}
      """
    Então o status da resposta deve ser 200
    E o corpo deve ter o campo "status" igual a "vigente"
    Quando eu envio GET para "/api/conformidade/GOV102"
    Então o corpo deve ter o campo "status" igual a "vigente"

  @negativo
  Cenário: Consultar um registro de conformidade que não existe
    Quando eu envio GET para "/api/conformidade/NAO_EXISTE"
    Então o status da resposta deve ser 404

  @negativo
  Cenário: Excluir um registro de conformidade que não existe
    Quando eu envio DELETE para "/api/conformidade/NAO_EXISTE"
    Então o status da resposta deve ser 404
