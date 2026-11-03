# language: pt
@social
Funcionalidade: Unidades com indicadores de inclusão e acessibilidade
  Para acompanhar metas sociais da cidade, quero cadastrar unidades com indicadores
  de diversidade, acessibilidade e programas sociais, que variam de uma unidade para outra.

  @positivo
  Cenário: Cadastrar uma unidade com indicadores de diversidade
    Quando eu envio POST para "/api/unidades" com o corpo:
      """
      {"id":"UNI100","nome":"Predio Publico Central","tipo":"publico","atributos":{"num_colaboradores":130,"diversidade":{"mulheres_lideranca_pct":52,"pcd_pct":11}}}
      """
    Então o status da resposta deve ser 201
    E o corpo deve respeitar o contrato "unidade.schema.json"
    E o corpo deve ter o campo "atributos.diversidade.pcd_pct" igual a "11"
    E o corpo deve ter o campo "atributos.diversidade.mulheres_lideranca_pct" igual a "52"

  @positivo
  Cenário: Cadastrar uma unidade residencial com dados de acessibilidade
    Quando eu envio POST para "/api/unidades" com o corpo:
      """
      {"id":"UNI101","nome":"Residencial Palmeiras","tipo":"residencial","atributos":{"num_moradores":48,"acessibilidade":{"rampa":true,"elevador":true}}}
      """
    Então o status da resposta deve ser 201
    E o corpo deve ter o campo "atributos.acessibilidade.rampa" igual a "true"

  @positivo
  Cenário: Atualizar a meta de diversidade de uma unidade
    Dado que existe em "/api/unidades" o registro:
      """
      {"id":"UNI102","nome":"Distrito Industrial","tipo":"industrial","atributos":{"diversidade":{"mulheres_lideranca_pct":18}}}
      """
    Quando eu envio PUT para "/api/unidades/UNI102" com o corpo:
      """
      {"nome":"Distrito Industrial","tipo":"industrial","atributos":{"diversidade":{"mulheres_lideranca_pct":24}}}
      """
    Então o status da resposta deve ser 200
    E o corpo deve ter o campo "atributos.diversidade.mulheres_lideranca_pct" igual a "24"

  @negativo
  Cenário: Consultar uma unidade que não existe
    Quando eu envio GET para "/api/unidades/NAO_EXISTE"
    Então o status da resposta deve ser 404

  @negativo
  Cenário: Atualizar uma unidade que não existe
    Quando eu envio PUT para "/api/unidades/NAO_EXISTE" com o corpo:
      """
      {"nome":"Fantasma","tipo":"publico","atributos":{}}
      """
    Então o status da resposta deve ser 404
