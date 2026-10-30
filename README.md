# Cadastro de Pacientes

Sistema simples em Java (Swing) para cadastro e listagem de pacientes, utilizando coleções (`ArrayList`), persistência em arquivo (`pacientes.csv`) e tratamento de exceções.

## Estrutura

- `src/Main.java`: Inicialização do programa.
- `src/model/Paciente.java`: Classe de modelo e validações dos dados.
- `src/repository/PacienteRepository.java`: Lista em memória (`ArrayList`) e leitura/escrita em arquivo.
- `src/exception/DadosInvalidosException.java`: Exceção customizada para validação.
- `src/view/`: Telas de cadastro e listagem em Swing.

## Como Executar

Pelo terminal, na pasta raiz do projeto:

```bash
javac -encoding UTF-8 -d out -sourcepath src src/Main.java
java -cp out Main
```

Ou execute a classe `src/Main.java` diretamente pela IDE.
