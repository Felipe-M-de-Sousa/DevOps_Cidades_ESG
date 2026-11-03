# language: pt
@governanca
Funcionalidade: Identificação do ambiente de execução
  Para ter rastreabilidade de qual versão está no ar, quero consultar em qual
  ambiente (local, staging ou production) a aplicação está rodando.

  @positivo
  Cenário: Consultar o ambiente em execução
    Quando eu envio GET para "/api/ambiente"
    Então o status da resposta deve ser 200
    E o corpo deve respeitar o contrato "ambiente.schema.json"
    E o corpo deve ter o campo "ambiente" igual a "staging"
    E o corpo deve ter o campo "versao" igual a "1.0.0"
