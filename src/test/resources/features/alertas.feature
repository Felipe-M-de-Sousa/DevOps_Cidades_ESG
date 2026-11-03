# language: pt
@ambiental @governanca
Funcionalidade: Alertas automáticos de consumo
  Para agir rápido quando um limite é ultrapassado, quero que a plataforma registre
  alertas, mostre apenas os que estão abertos e permita resolvê-los.

  Contexto:
    Dado que existe em "/api/alertas" o registro:
      """
      {"id":"ALT001","sensorId":"SENS002","unidadeId":"UNI002","tipoAlerta":"consumo_energia_excedido","severidade":"alta","status":"aberto","atributos":{"valor_limite":7.5,"valor_registrado":8.9}}
      """
    E que existe em "/api/alertas" o registro:
      """
      {"id":"ALT002","sensorId":"SENS008","unidadeId":"UNI002","tipoAlerta":"possivel_vazamento_agua","severidade":"critica","status":"aberto","atributos":{"acao_automatica_executada":"fechamento_valvula_remota"}}
      """
    E que existe em "/api/alertas" o registro:
      """
      {"id":"ALT003","sensorId":"SENS003","unidadeId":"UNI003","tipoAlerta":"qualidade_ar_ruim","severidade":"informativo","status":"resolvido","atributos":{}}
      """

  @positivo
  Cenário: Listar somente os alertas abertos
    Quando eu envio GET para "/api/alertas/abertos"
    Então o status da resposta deve ser 200
    E o corpo deve ser uma lista com 2 itens
    E todos os itens da lista devem respeitar o contrato "alerta.schema.json"
    E o corpo deve ter o campo "0.status" igual a "aberto"
    E o corpo deve ter o campo "1.status" igual a "aberto"

  @positivo
  Cenário: Alerta crítico registra a ação automática executada
    Quando eu envio GET para "/api/alertas/ALT002"
    Então o status da resposta deve ser 200
    E o corpo deve respeitar o contrato "alerta.schema.json"
    E o corpo deve ter o campo "severidade" igual a "critica"
    E o corpo deve ter o campo "atributos.acao_automatica_executada" igual a "fechamento_valvula_remota"

  @positivo
  Cenário: Resolver um alerta o remove da lista de abertos
    Quando eu envio PUT para "/api/alertas/ALT001" com o corpo:
      """
      {"sensorId":"SENS002","unidadeId":"UNI002","tipoAlerta":"consumo_energia_excedido","severidade":"alta","status":"resolvido","atributos":{}}
      """
    Então o status da resposta deve ser 200
    E o corpo deve ter o campo "status" igual a "resolvido"
    Quando eu envio GET para "/api/alertas/abertos"
    Então o corpo deve ser uma lista com 1 item

  @negativo
  Cenário: Resolver um alerta que não existe
    Quando eu envio PUT para "/api/alertas/NAO_EXISTE" com o corpo:
      """
      {"status":"resolvido","atributos":{}}
      """
    Então o status da resposta deve ser 404
