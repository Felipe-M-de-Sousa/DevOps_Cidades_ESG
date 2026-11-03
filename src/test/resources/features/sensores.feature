# language: pt
@ambiental
Funcionalidade: Gestão de sensores IoT
  Para monitorar o consumo de energia, água e a qualidade do ar da cidade,
  quero cadastrar, consultar e remover sensores IoT, cada um com os atributos do seu tipo.

  @positivo
  Cenário: Cadastrar um medidor de energia com sucesso
    Quando eu envio POST para "/api/sensores" com o corpo:
      """
      {"id":"SENS100","tipo":"medidor_energia","unidadeId":"UNI001","status":"ativo","atributos":{"potencia_kw":4.01,"limite_alerta_kw":8.0}}
      """
    Então o status da resposta deve ser 201
    E o corpo deve respeitar o contrato "sensor.schema.json"
    E o corpo deve ter o campo "id" igual a "SENS100"
    E o corpo deve ter o campo "atributos.potencia_kw" igual a "4.01"

  @positivo
  Esquema do Cenário: Sensores de tipos diferentes guardam atributos diferentes na mesma collection
    Quando eu envio POST para "/api/sensores" com o corpo:
      """
      {"id":"<id>","tipo":"<tipo>","status":"ativo","atributos":{"<atributo>":<valor>}}
      """
    Então o status da resposta deve ser 201
    E o corpo deve respeitar o contrato "sensor.schema.json"
    E o corpo deve ter o campo "atributos.<atributo>" igual a "<valor>"

    Exemplos:
      | id      | tipo            | atributo           | valor |
      | SENS201 | medidor_energia | potencia_kw        | 6.74  |
      | SENS202 | qualidade_ar    | pm25_ugm3          | 41.2  |
      | SENS203 | vazao_agua      | vazao_lmin         | 55.0  |
      | SENS204 | ocupacao        | pessoas_detectadas | 3     |

  @positivo
  Cenário: Cadastrar sensor sem informar o id gera um identificador automaticamente
    Quando eu envio POST para "/api/sensores" com o corpo:
      """
      {"tipo":"qualidade_ar","status":"ativo","atributos":{"pm25_ugm3":18.4}}
      """
    Então o status da resposta deve ser 201
    E o corpo deve conter o campo "id"
    E o corpo deve respeitar o contrato "sensor.schema.json"

  @positivo
  Cenário: Excluir um sensor desativado
    Dado que existe em "/api/sensores" o registro:
      """
      {"id":"SENS011","tipo":"qualidade_ar","status":"inativo","atributos":{}}
      """
    Quando eu envio DELETE para "/api/sensores/SENS011"
    Então o status da resposta deve ser 204
    Quando eu envio GET para "/api/sensores/SENS011"
    Então o status da resposta deve ser 404

  @negativo
  Cenário: Consultar um sensor que não existe
    Quando eu envio GET para "/api/sensores/NAO_EXISTE"
    Então o status da resposta deve ser 404

  @negativo
  Cenário: Rejeitar o cadastro de sensor com JSON malformado
    Quando eu envio POST para "/api/sensores" com o corpo:
      """
      {"tipo": "medidor_energia", "status":
      """
    Então o status da resposta deve ser 400

  @negativo
  Cenário: Excluir um sensor que não existe
    Quando eu envio DELETE para "/api/sensores/NAO_EXISTE"
    Então o status da resposta deve ser 404
