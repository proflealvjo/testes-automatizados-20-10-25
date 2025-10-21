# language: pt

@regressivo
Funcionalidade: Cadastro de entregas
  Como usuario da API de entregas
  Quero cadastrar multiplos tipos de entrega
  Para que o registro seja salvo corretamente

  Cenario: Cadastro bem-sucedido de entrega
    Dado que eu tenha os seguintes dados da entrega:
      | campo          | valor        |
      | numeroPedido   | 1            |
      | nomeEntregador | Ana Silva    |
      | statusEntrega  | EM_SEPARACAO |
      | dataEntrega    | 2024-08-22   |
    Quando eu enviar a requisição para o endpoint "/entregas"
    Então o status code da resposta deve ser 201

  @padrao
  Cenario: Cadastro de entrega sem sucesso ao passar o campo statusEntrega invalido
    Dado que eu tenha os seguintes dados da entrega:
      | campo          | valor      |
      | numeroPedido   | 1          |
      | nomeEntregador | Ana Silva  |
      | statusEntrega  | EM_ROTA    |
      | dataEntrega    | 2024-08-22 |
    Quando eu enviar a requisição para o endpoint "/entregas"
    Então o status code da resposta deve ser 400
    E o corpo de resposta de erro da api deve retornar a mensagem "Dados fornecidos estão em formato inválido."
