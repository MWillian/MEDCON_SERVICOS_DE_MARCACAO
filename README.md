README.md
# MEDCON - CONSULTAS MÉDICAS

Sistema de solicitação e marcação de procedimentos de saúde, focado no ambiente do sistema público de saúde.

## 1. Integrantes

*   CALEBE ALMEIDA MELO
*   JOSÉ PEDRO COSTA DUDA
*   MATEUS ALVES DOS SANTOS
*   MATHEUS WILLIAN DO NASCIMENTO OLIVEIRA
*   MELISSA CAROLYNE ALVES DE OLIVEIRA

## 2. Visão Geral do Projeto

O MEDCON é uma aplicação desenvolvida para gerenciar o fluxo de solicitação de exames e consultas. O sistema deve lidar com entidades como pacientes, médicos, técnicos de saúde, postos de saúde, procedimentos e horários.

A lógica central do sistema envolve um paciente que solicita a realização de um procedimento de saúde. O sistema deve retornar se é possível realizar o procedimento em um posto específico, verificando a disponibilidade do profissional e o horário, com um limite de marcações em até 30 dias.

O projeto atende aos requisitos de Orientação a Objetos (OO) e é estruturado em camadas (View, BO, VO, DAO).

## 3. Arquitetura e Estrutura de Camadas

A arquitetura utilizada é o **MVC (Model-View-Controller) adaptado para aplicações desktop/console**, também conhecida como Arquitetura em Camadas. O objetivo principal é seguir o Princípio da Responsabilidade Única, garantindo que cada parte do código tenha apenas uma razão para mudar.

| Camada | Nome Alternativo | O que faz | Classes Exemplo |
| :--- | :--- | :--- | :--- |
| **VO** | Model / DTO / Entity | Representa os dados puros, sem lógica de negócio complexa. Possui atributos privados, construtores, *getters/setters* . | `Paciente`, `Medico`, `Consulta`, `Pessoa` |
| **DAO** | Data Access Object | Responsável exclusivamente pela comunicação com o Banco de Dados. Utiliza JDBC. | `PacienteDAO`, `MedicoDAO`, `IBaseDAO<T>` (Interface) |
| **BO** | Business Object / Service | É o cérebro da aplicação, contendo toda a Regra de Negócio. Valida regras antes de chamar o DAO. | `AgendamentoBO` |
| **View** | Visualização | A interface com o usuário (console: `System.out.println` e `Scanner`). Captura dados e mostra mensagens, mas não deve conter regra de negócio. | `MenuPrincipal` |

### Estrutura de Pacotes

br.com.medcon

├── view        (Telas do console, menus)

├── bo          (Regras de negócio)

├── vo          (Entidades/Classes Básicas)

├── dao         (Acesso a dados)

├── exception   (Suas exceções personalizadas)

└── util        (Conexão com BD, leitores de teclado auxiliares)

## 4. Tecnologias Utilizadas (Stack)

O projeto está sendo desenvolvido em Java, utilizando a seguinte *stack* tecnológica:

*   **IDE:** Eclipse
*   **Banco de Dados:** SQLite (escolhido por funcionar baseado em arquivo e não exigir instalação de servidor)
*   **SGBD:** Dbeaver (para gerenciar e visualizar o banco)
*   **Conectividade:** JDBC (para conectar o Java ao SQLite)

## 5. Modelagem de Dados e OO

Para atender aos requisitos de OO, o modelo conceitual utiliza herança e polimorfismo:

*   **Herança e Abstração:** A classe `Pessoa` é Abstrata (não pode ser instanciada), e é herdada por `Paciente` e `Medico`.
*   **Polimorfismo:** Implementado através da interface genérica `IBaseDAO<T>`, que define o contrato de métodos (`salvar`, `atualizar`, `excluir`, `buscar`) para os diferentes DAOs (`PacienteDAO`, `MedicoDAO`).
*   **Collections:** Uso de `List<Consulta>` para o histórico do paciente e `Queue<Paciente>` (ou `PriorityQueue` se houver prioridade) para a fila de espera do posto.

## 6. Modelagem do Banco (Robustez)

O modelo de dados implementa uma separação clara entre a solicitação inicial e o agendamento confirmado, cumprindo o requisito de simular uma Fila Real:

*   **TB_SOLICITACAO:** Representa a Fila de Espera (o desejo do paciente por um serviço, mas ainda sem data definida).
*   **TB_AGENDAMENTO:** Representa o Compromisso firmado, cruzando a solicitação com a vaga livre na escala de trabalho.
*   **Hierarquia de Pessoas:** `TB_PESSOA` como base genérica, com `TB_PACIENTE` e `TB_PROFISSIONAL_SAUDE` herdando dela.
*   **Disponibilidade:** A `TB_LOTACAO` amarra o profissional ao posto, e a `TB_ESCALA_TRABALHO` define os horários fixos de atendimento.

