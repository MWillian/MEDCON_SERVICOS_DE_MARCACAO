# Centralizador de Consultas Médicas - Programação Orientada a Objetos - IFAL

## Índice

[Conceito do Projeto](#conceito-do-projeto)  
[Stack Tecnológica](#stack-tecnologica)  
[Atores Principais (Entidades do Domínio)](#atores-principais-entidades-do-dominio)  
[Arquitetura do Projeto](#arquitetura-do-projeto)  
[Estrutura de Camadas](#estrutura-de-camadas)  
[Requisitos Funcionais das Entidades](#requisitos-funcionais-das-entidades)  
&nbsp;&nbsp;&nbsp;&nbsp;[1. Pessoa (Classe Abstrata)](#1-pessoa-classe-abstrata)  
&nbsp;&nbsp;&nbsp;&nbsp;[2. Paciente](#2-paciente)  
&nbsp;&nbsp;&nbsp;&nbsp;[3. ProfissionalSaude](#3-profissionalsaude)  
&nbsp;&nbsp;&nbsp;&nbsp;[4. Especialidade](#4-especialidade)  
&nbsp;&nbsp;&nbsp;&nbsp;[5. PostoSaude](#5-postosaude)  
&nbsp;&nbsp;&nbsp;&nbsp;[6. TipoServico](#6-tiposervico)  
&nbsp;&nbsp;&nbsp;&nbsp;[7. Disponibilidade (A Grade Horária)](#7-disponibilidade-a-grade-horaria)  
&nbsp;&nbsp;&nbsp;&nbsp;[8. Agendamento (O Compromisso Firmado)](#8-agendamento-o-compromisso-firmado)  
[Estrutura do Banco de Dados](#estrutura-do-banco-de-dados)  
[Núcleo de Pessoas (Herança)](#nucleo-de-pessoas-heranca)  
[Dados Clínicos e Infraestrutura](#dados-clinicos-e-infraestrutura)  
[Serviços e Fluxo de Agendamento](#servicos-e-fluxo-de-agendamento)  

## Conceito do Projeto

Este projeto consiste no desenvolvimento de uma aplicação em Java para atuar como um centralizador de agendamentos para a rede pública de saúde. O objetivo principal é simular uma plataforma onde cidadãos podem solicitar marcações de consultas e exames em postos de saúde disponíveis na cidade.

O sistema gerencia a complexidade de alocar pacientes em horários disponíveis de profissionais (médicos, enfermeiros, técnicos), considerando a especialidade necessária, a lotação do profissional (em qual posto ele atende) e a duração do serviço diretamente na grade horária.

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
    - JDBC (Java Database Connectivity): API nativa do Java para conexão com o banco. Utilizada para escrever SQL manualmente, sem uso de ORMs (como Hibernate/Entity Framework).
    - DBeaver: Ferramenta visual para modelagem, criação de tabelas e consulta direta aos dados para verificação.
5. **Testes:**
    - JUnit: Framework para execução de testes unitários automatizados.
    - Mockito: Biblioteca para criação de objetos simulados (mocks), permitindo testar as regras de negócio isoladamente sem depender da conexão real com o banco de dados.

## Atores Principais (Entidades do Domínio)

1. **Pessoa**: A representação base de qualquer indivíduo no sistema, contendo dados demográficos comuns.
2. **Paciente**: O usuário final do sistema. Possui histórico médico e cartão do SUS.
3. **Profissional de Saúde**: Médicos, enfermeiros ou técnicos. Possuem registros profissionais (CRM/COREN) e especialidades definidas.
4. **Especialidade**: A qualificação do profissional (ex: Cardiologia, Raio-X), fundamental para filtrar quem pode realizar qual serviço.
5. **Posto de Saúde**: A unidade física onde o atendimento ocorre. Um profissional pode atuar em múltiplos postos.
6. **Serviço**: O procedimento a ser realizado (ex: Consulta de Rotina, Exame de Sangue), com duração definida.
7. **Disponibilidade**: A grade de horário fixa de um profissional em um determinado posto (ex: Segundas, das 08h às 12h).
8. **Agendamento**: A concretização do serviço. Ocorre quando o paciente agenda diretamente em um horário compatível na agenda de um profissional.

## Arquitetura do Projeto

A aplicação é um Console Application (executada via terminal), estruturada no padrão de arquitetura em camadas (MVC simplificado) para garantir a separação de responsabilidades e desacoplamento.

## Estrutura de Camadas

1. **View (Camada de Apresentação)**:
    - Responsável pela interação direta com o usuário via terminal.
    - Exibe os menus (MenuAdminView e MenuPacienteView), captura os dados digitados (inputs) e mostra as respostas processadas (outputs).
    - Atua como o "Controller" do fluxo, chamando as camadas inferiores baseada na escolha do usuário.

2. **BO - Business Object (Camada de Negócio)**:
    - O "cérebro" da aplicação. Contém toda a inteligência e validação (AgendamentoBO, PacienteBO, etc).
    - Não acessa o banco de dados diretamente e não interage com o usuário.
    - Valida regras como: conflito de horários (cálculo de slots de tempo), verificação de elegibilidade do paciente e disponibilidade de profissionais.

3. **VO - Value Object (Objeto de Valor / Modelo)**:
    - Representa as entidades puras do sistema.
    - São classes simples que contêm apenas atributos (dados) e métodos de acesso (encapsulamento), sem lógica complexa.
    - Servem para transportar dados entre as camadas (do Banco para a Tela e vice-versa).

4. **DAO - Data Access Object (Camada de Acesso a Dados)**:
    - Responsável exclusivo pela persistência (AgendamentoDAO, ConexaoSQLite, etc).
    - É a única camada que conhece SQL.
    - Realiza as operações de CRUD (Create, Read, Update, Delete) no banco de dados SQLite e converte os registros da tabela em objetos Java (VOs) para uso da aplicação.

## Requisitos Funcionais das Entidades

### 1. Pessoa (Classe Abstrata)

Como Pessoa é uma classe abstrata que serve de base, seus requisitos são de estrutura e herança, focados em gerenciar dados demográficos comuns a pacientes e profissionais de saúde.

    RF1.1 - Gerenciamento de Dados Comuns: A entidade deve permitir o armazenamento e acesso de dados básicos de identificação, como nome completo, CPF e data de nascimento (atributos que serão herdados).
    RF1.2 - Unicidade: Deve-se garantir a unicidade do CPF no sistema para evitar duplicação de cadastros, seja de pacientes ou profissionais.
    RF1.3 - Atualização de Contato: Deve permitir a alteração e atualização dos dados de telefone e endereço do indivíduo.

### 2. Paciente

A entidade Paciente representa o usuário do sistema que busca atendimento. Seus requisitos são focados no cadastro e no vínculo com o histórico médico.

    RF2.1 - Cadastro Completo: Deve permitir o cadastro de um novo paciente no sistema, exigindo todos os dados de Pessoa (herdado) mais o número do Cartão SUS.
    RF2.2 - Inclusão de Histórico: Deve permitir a adição de novos registros à lista de histórico médico do paciente (como alergias, cirurgias ou doenças crônicas).
    RF2.3 - Consulta de Histórico: Deve permitir a visualização completa de todos os registros do histórico médico (1:N) de um paciente específico.
    RF2.4 - Busca: Deve permitir a busca de um paciente já cadastrado utilizando seu CPF ou Cartão SUS.

### 3. ProfissionalSaude

Representa a força de trabalho (Médicos, Enfermeiros, Técnicos). É uma especialização de Pessoa, mas com atributos regulatórios rígidos.

    RF3.1 - Cadastro e Credenciamento: O sistema deve permitir o cadastro de profissionais, validando a unicidade do Registro Profissional (CRM, COREN, etc.) para evitar duplicidade ou exercício ilegal dentro da plataforma.
    RF3.2 - Definição de Papel: Deve ser possível categorizar o profissional pelo seu tipo de atuação (Médico, Enfermeiro, Técnico), pois isso restringirá quais serviços ele pode realizar.
    RF3.3 - Vínculo de Especialidade: Cada profissional deve estar associado a uma Especialidade principal (ex: Pediatria), definindo seu escopo de atuação técnica.
    RF3.4 - Multilotação (Vínculo com Postos): O sistema deve permitir que um único profissional seja vinculado a vários Postos de Saúde diferentes (relação N:N), representando a realidade de médicos que atendem em múltiplas unidades.

### 4. Especialidade

Esta entidade serve como um "filtro" ou "categoria" macro para organizar os profissionais e os serviços.

    RF4.1 - Manutenção do Catálogo: O sistema deve permitir o cadastro, alteração e listagem de especialidades médicas e técnicas (ex: Cardiologia, Radiologia, Clínica Geral).
    RF4.2 - Informação Descritiva: Cada especialidade deve possuir um campo de descrição ou detalhamento, para auxiliar o usuário leigo a entender do que se trata aquela área médica no momento da busca.
    RF4.3 - Filtragem: A entidade deve servir como critério de agrupamento, permitindo que o sistema liste "Todos os profissionais da especialidade X".

### 5. PostoSaude

Representa a unidade física de atendimento (UBS, Clínica da Família). É o "Onde".

    RF5.1 - Gestão da Unidade: O sistema deve permitir o cadastro e manutenção das informações dos postos de saúde, incluindo obrigatoriamente nome e endereço completo.
    RF5.2 - Consulta de Localização: O paciente deve conseguir visualizar a lista de postos disponíveis, com informações claras de endereço.
    RF5.3 - Listagem de Corpo Clínico: Deve ser possível consultar quais profissionais estão lotados (trabalham) em um determinado posto.

### 6. TipoServico

Representa o procedimento em si (Consulta, Exame). É o "O Quê".

    RF6.1 - Definição do Procedimento: O sistema deve permitir cadastrar os tipos de serviços oferecidos pela rede (ex: "Consulta Pediátrica", "Raio-X de Tórax").
    RF6.2 - Padronização de Duração: Cada serviço deve ter uma duração estimada em minutos (ex: Consulta = 30min, Raio-X = 15min).
    RF6.3 - Restrição de Competência: Cada serviço deve estar vinculado a uma Especialidade necessária.

### 7. Disponibilidade (A Grade Horária)

Esta entidade não representa uma consulta marcada, mas sim a "janela de tempo" em que o médico pode trabalhar. É a configuração da agenda.

    RF7.1 - Definição de Grade: O sistema deve permitir cadastrar os horários fixos de atendimento de um profissional em um posto específico.
    RF7.2 - Validação de Choque de Horário: O sistema não pode permitir que o mesmo profissional tenha duas disponibilidades conflitantes no mesmo horário.

### 8. Agendamento (O Compromisso Firmado)

É a concretização do atendimento. É o objeto que liga o Paciente, o Médico, o Posto e a Hora exata.

    RF8.1 - Consolidação da Agenda: O sistema deve gerar um Agendamento num determinado intervalo de tempo dentro dos limites livres da grade do profissional especificado.
    RF8.2 - Bloqueio de Horário (Slot): Ao criar um agendamento, o sistema calcula os horários de início e de fim baseando-se na duração do serviço (definido no TipoServico), garantindo que os próximos agendamentos não se sobreponham.
    RF8.3 - Registro de Conclusão: Após a data do atendimento, o sistema deve registrar e prover acompanhamento de status do Agendamento (ex: REALIZADA, NAO_COMPARECEU, AGENDADA).
    RF8.4 - Emissão de Laudo/Resultado: Pode conter registro em laudo associado ao fluxo do paciente nos agendamentos concluídos.

## Estrutura do Banco de Dados 

### Núcleo de Pessoas (Herança)

`tb_pessoa (Entidade Pai)`
- Descrição: Armazena os dados comuns a qualquer indivíduo no sistema.
- Função: Centralizar Nome, CPF, Data de Nascimento, Endereço e Telefone.

`tb_paciente (Entidade Filha)`
- Descrição: Armazena dados exclusivos de quem recebe atendimento.
- Função: Guarda o número do Cartão SUS e atua como vínculo principal para históricos.
- Relação: Possui FK para tb_pessoa.

`tb_profissional (Entidade Filha)`
- Descrição: Armazena dados de quem presta o serviço (Médicos, Enfermeiros, Técnicos).
- Função: Guarda o Registro Profissional e define o Tipo de Profissional, atrelado a uma tb_especialidade.

### Dados Clínicos e Infraestrutura

`tb_especialidade`
- Descrição: Catálogo de qualificações médicas.
- Função: Define áreas de atuação médica, referenciada por profissionais e serviços.

`tb_posto`
- Descrição: Unidades físicas de saúde.
- Função: Representa o local do atendimento.

`tb_profissional_posto (Tabela Associativa)`
- Descrição: Vínculo N:N (Muitos para Muitos) entre Profissionais e Postos.
- Função: Permite que um mesmo médico trabalhe em múltiplos postos de saúde de forma simultânea.

### Serviços e Fluxo de Agendamento

`tb_tipo_servico`
- Descrição: O "Cardápio" de serviços oferecidos com referências de durações e qualificações requeridas.

`tb_disponibilidade`
- Descrição: A grade horária fixa (Configuração temporal do profissional).
- Função: Define quando um profissional está apto a trabalhar em um determinado posto (Dia da Semana + Hora Início + Hora Fim).

`tb_agendamento (Compromisso Confirmado)`
- Descrição: A consulta ou exame consumado como operação atômica de banco de dados e de domínio entre o Paciente e a Disponibilidade.
- Função: Cruza temporalmente os slots ocupados do Médico, definindo e concretizando quando algo irá acontecer, controlando status e eventuais laudos atestativos.
