# Centralizador de Consultas Médicas - Programação Orientada a Objetos - IFAL

## Índice

[Conceito do Projeto](#conceito-do-projeto)  
[Stack Tecnológica](#stack-tecnológica)  
[Atores Principais (Entidades do Domínio)](#atores-principais-entidades-do-domínio)  
[Arquitetura do Projeto](#arquitetura-do-projeto)  
[Estrutura de Camadas](#estrutura-de-camadas)  
[Requisitos Funcionais das Entidades](#requisitos-funcionais-das-entidades)  
&nbsp;&nbsp;&nbsp;&nbsp;[1. Pessoa (Classe Abstrata)](#1-pessoa-classe-abstrata)  
&nbsp;&nbsp;&nbsp;&nbsp;[2. Paciente](#2-paciente)  
&nbsp;&nbsp;&nbsp;&nbsp;[3. ProfissionalSaude](#3-profissionalsaude)  
&nbsp;&nbsp;&nbsp;&nbsp;[4. Especialidade](#4-especialidade)  
&nbsp;&nbsp;&nbsp;&nbsp;[5. PostoSaude](#5-postosaude)  
&nbsp;&nbsp;&nbsp;&nbsp;[6. TipoServico](#6-tiposervico)  
&nbsp;&nbsp;&nbsp;&nbsp;[7. Disponibilidade (A Grade Horária)](#7-disponibilidade-a-grade-horária)  
&nbsp;&nbsp;&nbsp;&nbsp;[8. Agendamento (O Compromisso Firmado)](#8-agendamento-o-compromisso-firmado)  
[Estrutura do Banco de Dados](#estrutura-do-banco-de-dados)  
[Núcleo de Pessoas (Herança)](#núcleo-de-pessoas-herança)  
[Dados Clínicos e Infraestrutura](#dados-clínicos-e-infraestrutura)  
[Serviços e Fluxo de Agendamento](#serviços-e-fluxo-de-agendamento)  


## Conceito do Projeto

Este projeto consiste no desenvolvimento de uma aplicação em Java para atuar como um centralizador de agendamentos para a rede pública de saúde. O objetivo principal é simular uma plataforma onde cidadãos podem solicitar marcações de consultas e exames em postos de saúde disponíveis na cidade.

O sistema gerencia a complexidade de alocar pacientes em horários disponíveis de profissionais (médicos, enfermeiros, técnicos), considerando a especialidade necessária, a lotação do profissional (em qual posto ele atende) e a gestão de filas de espera quando não há disponibilidade imediata.

## Stack Tecnológica

A escolha das ferramentas visa atender aos requisitos acadêmicos de "Java Puro" (sem frameworks pesados), garantindo portabilidade e foco nos fundamentos da Orientação a Objetos.

1. **Linguagem & Runtime**:
    
    - Java (JDK 21): Linguagem core do projeto, utilizada para toda a lógica de backend e estruturação OO.
2. **Ambiente de Desenvolvimento**:
    
    - VS Code: Editor de código leve, utilizado com o Extension Pack for Java para compilação e execução.
3. **Gerenciamento de Projeto**:
    
    - Maven: Responsável pelo gerenciamento de dependências (bibliotecas externas) e automação do build/compilação do projeto.
4. **Banco de Dados**:

    - SQLite: Banco de dados relacional serverless (baseado em arquivo), escolhido pela portabilidade e facilidade de configuração sem necessidade de instalação de serviços de banco.

    - JDBC (Java Database Connectivity): API nativa do Java para conexão com o banco. Será utilizada para escrever SQL manualmente, sem uso de ORMs (como Hibernate/Entity Framework).

    - DBeaver: Ferramenta visual para modelagem, criação de tabelas e consulta direta aos dados para verificação.

5. **Testes:**

    - JUnit: Framework para execução de testes unitários automatizados.

    - Mockito: Biblioteca para criação de objetos simulados (mocks), permitindo testar as regras de negócio isoladamente sem depender da conexão real com o banco de dados.

## Atores Principais (Entidades do Domínio)

1. **Pessoa**: A representação base de qualquer indivíduo no sistema, contendo dados demográficos comuns.
2. **Paciente**: O usuário final do sistema. Possui histórico médico e cartão do SUS, sendo o originador das solicitações.
3. **Profissional de Saúde**: Médicos, enfermeiros ou técnicos. Possuem registros profissionais (CRM/COREN) e especialidades definidas.
4. **Especialidade**: A qualificação do profissional (ex: Cardiologia, Raio-X), fundamental para filtrar quem pode realizar qual serviço.
5. **Posto de Saúde**: A unidade física onde o atendimento ocorre. Um profissional pode atuar em múltiplos postos.
6. **Serviço**: O procedimento a ser realizado (ex: Consulta de Rotina, Exame de Sangue), com duração definida.
7. **Disponibilidade**: A grade de horário fixa de um profissional em um determinado posto (ex: Segundas, das 08h às 12h).
8. **Solicitação (Fila)**: O pedido inicial do paciente. Representa a entrada em uma fila de espera antes da confirmação.
9. **Agendamento**: A concretização do serviço. Ocorre quando uma solicitação encontra uma disponibilidade compatível na agenda.

## Arquitetura do Projeto

A aplicação é um Console Application (executada via terminal), estruturada no padrão de arquitetura em camadas (MVC simplificado) para garantir a separação de responsabilidades e desacoplamento.

## Estrutura de Camadas:

1. **View (Camada de Apresentação)**:

    - Responsável pela interação direta com o usuário via terminal.
    - Exibe os menus, captura os dados digitados (inputs) e mostra as respostas processadas (outputs).
    - Atua como o "Controller" do fluxo, chamando as camadas inferiores baseada na escolha do usuário.

2. **BO - Business Object (Camada de Negócio)**:

    - O "cérebro" da aplicação. Contém toda a inteligência e validação.
    - Não acessa o banco de dados diretamente e não interage com o usuário.
    - Valida regras como: conflito de horários, verificação de elegibilidade do paciente, priorização de filas e disponibilidade de profissionais.

3. **VO - Value Object (Objeto de Valor / Modelo)**:

    - Representa as entidades puras do sistema.
    - São classes simples (POJOs) que contêm apenas atributos (dados) e métodos de acesso (encapsulamento), sem lógica complexa.
    - Servem para transportar dados entre as camadas (do Banco para a Tela e vice-versa).

4. **DAO - Data Access Object (Camada de Acesso a Dados)**:

    - Responsável exclusivo pela persistência.
    - É a única camada que conhece SQL.
    - Realiza as operações de CRUD (Create, Read, Update, Delete) no banco de dados SQLite e converte os registros da tabela em objetos Java (VOs) para uso da aplicação.

## Requisitos funcionais das entidades

### 1. Pessoa (Classe Abstrata) ###

Como Pessoa é uma classe abstrata que serve de base, seus requisitos são de estrutura e herança, focados em gerenciar dados demográficos comuns a pacientes e profissionais de saúde.

    RF1.1 - Gerenciamento de Dados Comuns: A entidade deve permitir o armazenamento e acesso de dados básicos de identificação, como nome completo, CPF e data de nascimento (atributos que serão herdados).

    RF1.2 - Unicidade: Deve-se garantir a unicidade do CPF no sistema para evitar duplicação de cadastros, seja de pacientes ou profissionais.

    RF1.3 - Atualização de Contato: Deve permitir a alteração e atualização dos dados de telefone e endereço do indivíduo

### 2. Paciente

A entidade Paciente representa o usuário do sistema que busca atendimento. Seus requisitos são focados no cadastro e no vínculo com o histórico médico.

    RF2.1 - Cadastro Completo: Deve permitir o cadastro de um novo paciente no sistema, exigindo todos os dados de Pessoa (herdado) mais o número do Cartão SUS.

    RF2.2 - Inclusão de Histórico: Deve permitir a adição de novos registros à lista de histórico médico do paciente (como alergias, cirurgias ou doenças crônicas).

    RF2.3 - Consulta de Histórico: Deve permitir a visualização completa de todos os registros do histórico médico (1:N) de um paciente específico.

    RF2.4 - Busca: Deve permitir a busca de um paciente já cadastrado utilizando seu CPF ou Cartão SUS.

### 3. ProfissionalSaude

Representa a força de trabalho (Médicos, Enfermeiros, Técnicos). É uma especialização de Pessoa, mas com atributos regulatórios rígidos.

    RF3.1 - Cadastro e Credenciamento: O sistema deve permitir o cadastro de profissionais, validando a unicidade do Registro Profissional (CRM, COREN, etc.) para evitar duplicidade ou exercício ilegal dentro da plataforma.

    RF3.2 - Definição de Papel: Deve ser possível categorizar o profissional pelo seu tipo de atuação (Médico, Enfermeiro, Técnico), pois isso restringirá quais serviços ele pode realizar (ex: um Técnico não faz consulta médica).

    RF3.3 - Vínculo de Especialidade: Cada profissional deve estar associado a uma Especialidade principal (ex: Pediatria), definindo seu escopo de atuação técnica.

    RF3.4 - Multilotação (Vínculo com Postos): O sistema deve permitir que um único profissional seja vinculado a vários Postos de Saúde diferentes (relação N:N), representando a realidade de médicos que atendem em múltiplas unidades.

### 4. Especialidade

Esta entidade serve como um "filtro" ou "categoria" macro para organizar os profissionais e os serviços.

    RF4.1 - Manutenção do Catálogo: O sistema deve permitir o cadastro, alteração e listagem de especialidades médicas e técnicas (ex: Cardiologia, Radiologia, Clínica Geral).

    RF4.2 - Informação Descritiva: Cada especialidade deve possuir um campo de descrição ou detalhamento, para auxiliar o usuário leigo a entender do que se trata aquela área médica no momento da busca (ex: "Dermatologia: Cuida de doenças de pele, cabelos e unhas").

    RF4.3 - Filtragem: A entidade deve servir como critério de agrupamento, permitindo que o sistema liste "Todos os profissionais da especialidade X".

### 5. PostoSaude

Representa a unidade física de atendimento (UBS, Clínica da Família). É o "Onde".

    RF5.1 - Gestão da Unidade: O sistema deve permitir o cadastro e manutenção das informações dos postos de saúde, incluindo obrigatoriamente nome e endereço completo.

    RF5.2 - Consulta de Localização: O paciente deve conseguir visualizar a lista de postos disponíveis, com informações claras de endereço para saber onde se dirigir.

    RF5.3 - Listagem de Corpo Clínico: Deve ser possível consultar quais profissionais estão lotados (trabalham) em um determinado posto. Isso é essencial para que o usuário saiba se o "Dr. João" atende no "Posto Centro" ou no "Posto Bairro Sul".

### 6. TipoServico

Representa o procedimento em si (Consulta, Exame). É o "O Quê".

    RF6.1 - Definição do Procedimento: O sistema deve permitir cadastrar os tipos de serviços oferecidos pela rede (ex: "Consulta Pediátrica", "Raio-X de Tórax").

    RF6.2 - Padronização de Duração: Cada serviço deve ter uma duração estimada em minutos (ex: Consulta = 30min, Raio-X = 15min).

    RF6.3 - Restrição de Competência: Cada serviço deve estar vinculado a uma Especialidade necessária.
        Exemplo: O serviço "Ecocardiograma" só pode ser realizado por um profissional com especialidade "Cardiologia". O sistema deve impedir que um "Dermatologista" seja agendado para fazer esse serviço.

### 7. Disponibilidade (A Grade Horária)

Esta entidade não representa uma consulta marcada, mas sim a "janela de tempo" em que o médico pode trabalhar. É a configuração da agenda.

    RF7.1 - Definição de Grade: O sistema deve permitir cadastrar os horários fixos de atendimento de um profissional em um posto específico (ex: "Dr. Ana atende no Posto Central, às Segundas-feiras, das 08:00 às 12:00").

    RF7.2 - Validação de Choque de Horário: O sistema não pode permitir que o mesmo profissional tenha duas disponibilidades conflitantes no mesmo horário, mesmo que em postos diferentes (ex: Estar no Posto A e no Posto B na segunda de manhã ao mesmo tempo)

### 8. Agendamento (O Compromisso Firmado) ###

É a concretização do atendimento. É o objeto que liga o Paciente, o Médico, o Posto e a Hora exata.

    RF9.1 - Consolidação da Agenda: O sistema deve gerar um Agendamento cruzando uma Solicitação Pendente com uma Disponibilidade livre de um profissional apto (que tenha a especialidade necessária para o serviço).

    RF9.2 - Bloqueio de Horário (Slot): Ao criar um agendamento, o sistema deve calcular o horário de fim baseando-se na duração do serviço (definido no TipoServico) e garantir que nenhum outro paciente seja agendado para o mesmo médico nesse intervalo.

    RF9.3 - Registro de Conclusão: Após a data do atendimento, o sistema deve permitir que o médico (ou atendente) altere o status para REALIZADA ou NAO_COMPARECEU.

    RF9.4 - Emissão de Laudo/Resultado: Se o agendamento for concluído, deve ser possível anexar um texto de laudo ou observações médicas ao registro (que ficará visível no histórico do agendamento).


## Estrutura do Banco de Dados 

### Núcleo de Pessoas (Herança)

Estas tabelas centralizam os dados demográficos para evitar duplicidade de informações.

`tb_pessoa (Entidade Pai)`

    - Descrição: Armazena os dados comuns a qualquer indivíduo no sistema.
    - Função: Centralizar Nome, CPF (Unique), Data de Nascimento, Endereço e Telefone.
    - Relação: Serve de base para tb_paciente e tb_profissional.

`tb_paciente (Entidade Filha)`

    - Descrição: Armazena dados exclusivos de quem recebe atendimento.
    - Função: Guarda o número do Cartão SUS e atua como vínculo principal para históricos e solicitações.
    - Relação: Possui uma Chave Estrangeira (FK) para tb_pessoa que também atua como sua Chave Primária (PK).

`tb_profissional (Entidade Filha)`

        - Descrição: Armazena dados de quem presta o serviço (Médicos, Enfermeiros, Técnicos).
        - Função: Guarda o Registro Profissional (CRM/COREN) e define o Tipo de Profissional.
        - Relação: Vincula-se a uma Especialidade (tb_especialidade) e estende tb_pessoa.

### Dados Clínicos e Infraestrutura

Tabelas que definem "Onde" o atendimento ocorre e "Quem" pode realizá-lo.

`tb_especialidade`

    - Descrição: Catálogo de qualificações médicas.
    - Função: Define áreas de atuação (ex: Cardiologia, Pediatria) e serve de filtro para quais serviços um profissional pode realizar.

`tb_posto`

    - Descrição: Unidades físicas de saúde.
    - Função: Representa o local do atendimento (ex: UBS Centro, Policlínica Sul) com endereço e nome.

`tb_profissional_posto (Tabela Associativa)`

    - Descrição: Vínculo N:N (Muitos para Muitos) entre Profissionais e Postos.

    - Função: Permite que um mesmo médico trabalhe em múltiplos postos de saúde diferentes. Sem esta tabela, o médico ficaria "preso" a um único local.

### Serviços e Fluxo de Agendamento

O coração do sistema, controlando a oferta (Disponibilidade) e a demanda (Solicitação/Agendamento).

`tb_tipo_servico`

    - Descrição: O "Cardápio" de serviços oferecidos.

    - Função: Define o nome do procedimento (ex: Consulta, Raio-X) e, crucialmente, a Duração Média (para cálculo de agenda) e a Especialidade Necessária (para impedir que um dermatologista faça uma cirurgia cardíaca).

`tb_disponibilidade`

    - Descrição: A grade horária fixa (Configuração).

    - Função: Define quando um profissional está apto a trabalhar em um determinado posto (Dia da Semana + Hora Início + Hora Fim). Não é a consulta em si, mas a "janela de oportunidade".

`tb_agendamento (Compromisso Confirmado)`

    - Descrição: A consulta ou exame marcado com data e hora exatas.

    - Função: É o resultado do cruzamento de uma Solicitação com uma Disponibilidade.

    - Regra Crítica: Possui vínculo 1:1 com tb_solicitacao (Unique), garantindo que um pedido da fila gere apenas um compromisso real. Armazena também o status final (Realizada/Cancelada).
