# language: pt
@ambiental
Funcionalidade: Registro de leituras de consumo
  Para acompanhar a eficiência energética, quero registrar e consultar as leituras
  de energia, água e qualidade do ar enviadas pelos sensores.

  @positivo
  Cenário: Registrar uma leitura de energia
    Quando eu envio POST para "/api/leituras" com o corpo:
      """
      {"id":"LEIT100","sensorId":"SENS001","unidadeId":"UNI001","tipoMedida":"energia_kwh","valor":96.4,"timestamp":"2026-08-01T10:00:00Z"}
      """
    Então o status da resposta deve ser 201
    E o corpo deve respeitar o contrato "leitura.schema.json"
    E o corpo deve ter o campo "valor" igual a "96.4"
    E o corpo deve ter o campo "timestamp" igual a "2026-08-01T10:00:00Z"

  @positivo
  Cenário: Listar as leituras registradas
    Dado que existe em "/api/leituras" o registro:
      """
      {"id":"LEIT101","sensorId":"SENS001","unidadeId":"UNI001","tipoMedida":"energia_kwh","valor":90.1}
      """
    E que existe em "/api/leituras" o registro:
      """
      {"id":"LEIT102","sensorId":"SENS004","unidadeId":"UNI004","tipoMedida":"agua_litros","valor":17712}
      """
    Quando eu envio GET para "/api/leituras"
    Então o status da resposta deve ser 200
    E o corpo deve ser uma lista com 2 itens
    E todos os itens da lista devem respeitar o contrato "leitura.schema.json"

  @negativo
  Cenário: Rejeitar leitura com valor que não é numérico
    Quando eu envio POST para "/api/leituras" com o corpo:
      """
      {"sensorId":"SENS001","tipoMedida":"energia_kwh","valor":"muito alto"}
      """
    Então o status da resposta deve ser 400

  @negativo
  Cenário: Consultar uma leitura que não existe
    Quando eu envio GET para "/api/leituras/NAO_EXISTE"
    Então o status da resposta deve ser 404
