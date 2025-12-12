package br.com.medcon.view;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import br.com.medcon.bo.AgendamentoBO;
import br.com.medcon.bo.DisponibilidadeBO;
import br.com.medcon.bo.EspecialidadeBO;
import br.com.medcon.bo.PacienteBO;
import br.com.medcon.bo.PostoSaudeBO;
import br.com.medcon.bo.ProfissionalPostoBO;
import br.com.medcon.bo.TipoServicoBO;
import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.enums.StatusAgendamento;
import br.com.medcon.vo.Agendamento;
import br.com.medcon.vo.Disponibilidade;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.Paciente;
import br.com.medcon.vo.TipoServico;

public class MenuPacienteView {
    private final Scanner scanner;
    private final PacienteBO pacienteBO;
    private Paciente pacienteLogado;
    private final TipoServicoBO tipoServicoBO;
    private final DisponibilidadeBO disponibilidadeBO;
    private final AgendamentoBO agendamentoBO;

    public MenuPacienteView(
            Scanner scanner,
            PacienteBO pacienteBO,
            TipoServicoBO tipoServicoBO,
            EspecialidadeBO especialidadeBO,
            DisponibilidadeBO disponibilidadeBO,
            AgendamentoBO agendamentoBO,
            ProfissionalPostoBO profissionalPostoBO,
            PostoSaudeBO postoSaudeBO) {
        this.scanner = scanner;
        this.pacienteBO = pacienteBO;
        this.tipoServicoBO = tipoServicoBO;
        this.disponibilidadeBO = disponibilidadeBO;
        this.agendamentoBO = agendamentoBO;
    }

    public void iniciar() throws NegocioException {
        while (true) {
            System.out.println("\n=== ÁREA DO PACIENTE ===");
            System.out.println("1. Já tenho cadastro (Login)");
            System.out.println("2. Quero me cadastrar");
            System.out.println("3. Voltar ao Menu Principal");
            System.out.print("> Opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1" -> fazerLogin();
                case "2" -> cadastrarPaciente();
                case "3" -> {
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void fazerLogin() throws NegocioException {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Digite seu CPF (apenas números): ");
        String cpf = scanner.nextLine();
        try {
            Paciente p = pacienteBO.buscarPorCpf(cpf);

            this.pacienteLogado = p;
            System.out.println("Bem-vindo de volta, " + p.getNome() + "!");
            exibirMenuLogado();

        } catch (SQLException e) {
            System.out.println("Erro no sistema: " + e.getMessage());
        } catch (NegocioException e) {
            System.out.println(e.getMessage());
        }
    }

    private void cadastrarPaciente() {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            System.out.println("\n--- NOVO CADASTRO ---");

            System.out.print("Nome Completo: ");
            String nome = scanner.nextLine();
            pacienteBO.validarNome(nome);

            System.out.print("CPF (XXX.XXX.XXX-XX): ");
            String cpf = scanner.nextLine();
            pacienteBO.validarCpf(cpf);

            System.out.print("Data Nascimento (dd/MM/yyyy): ");
            String dataTexto = scanner.nextLine();
            LocalDate dataNasc = LocalDate.parse(dataTexto, formatador);
            pacienteBO.validarDataNascimento(dataNasc);

            System.out.print("Telefone: ");
            String fone = scanner.nextLine();
            pacienteBO.validarTelefone(fone);

            System.out.print("Endereço: ");
            String endereco = scanner.nextLine();
            pacienteBO.validarEndereco(endereco);

            System.out.print("Cartão SUS: ");
            String cartaoSus = scanner.nextLine();
            pacienteBO.validarCartaoSus(cartaoSus);

            Paciente novoPaciente = new Paciente(0, nome, cpf, dataNasc, fone, endereco, cartaoSus);
            pacienteBO.salvar(novoPaciente);

            System.out.println("Cadastro realizado com sucesso! Bem-vindo(a), " + nome + "!");
            this.pacienteLogado = novoPaciente;
            exibirMenuLogado();

        } catch (NegocioException e) {
            System.out.println(e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Erro: Data inválida. Use o formato dd/MM/yyyy.");
        } catch (SQLException e) {
            System.out.println("Erro interno no sistema. Tente novamente mais tarde.");
        }
    }

    private void exibirMenuLogado() throws NegocioException {
        while (pacienteLogado != null) {
            System.out.println("\n--- OLÁ, " + pacienteLogado.getNome().toUpperCase() + " ---");
            System.out.println("1. Nova Solicitação de Agendamento");
            System.out.println("2. Meus Agendamentos");
            System.out.println("3. Meus Dados Cadastrais");
            System.out.println("0. Sair (Deslogar)");
            System.out.print("> Opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1" -> novaSolicitacao();
                case "2" -> exibirAgendamentos();
                case "3" -> mostrarDados();
                case "0" -> {
                    this.pacienteLogado = null;
                    System.out.println("Deslogado com sucesso.");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void novaSolicitacao() throws NegocioException {
        System.out.println("\n=== NOVO AGENDAMENTO: SELECIONE O SERVIÇO ===");
        try {
            List<TipoServico> servicos = tipoServicoBO.listarTodos();
            if (servicos.isEmpty()) {
                System.out.println("Nenhum serviço cadastrado no sistema no momento.");
                return;
            }

            for (TipoServico s : servicos) {
                System.out.printf("[%d] - %s (Duração Média: %d min)%n",
                        s.getId(), s.getNome(), s.getDuracaoMinutos());
            }

            System.out.println("[0] - Voltar");
            System.out.print("> Digite o ID do serviço desejado: ");
            String entrada = scanner.nextLine();

            if (entrada.equals("0")) {
                return;
            }

            int idServico = Integer.parseInt(entrada);
            TipoServico servicoSelecionado = tipoServicoBO.buscarPorId(idServico);

            if (servicoSelecionado == null) {
                System.out.println("Serviço inválido.");
                return;
            }

            System.out.println("Selecionado: " + servicoSelecionado.getNome());

            Especialidade esp = servicoSelecionado.getEspecialidadeNecessaria();
            System.out.println("Especialidade requerida: " + esp.getNome());

            Disponibilidade disponibilidade = escolherDisponibilidade(esp);

            if (disponibilidade != null) {
                LocalDate dataAgendamento = calcularProximaData(disponibilidade.getDiaSemana());
                LocalTime horaInicio = disponibilidade.getHoraInicio();
                LocalDateTime dataHoraInicio = LocalDateTime.of(dataAgendamento, horaInicio);

                System.out.println("\n=== CONFIRMAÇÃO ===");
                System.out.println("Serviço: " + servicoSelecionado.getNome());
                System.out.println("Profissional: " + disponibilidade.getProfissional().getNome());
                System.out.println("Local: " + disponibilidade.getPosto().getNome());
                System.out.println("Data: " + dataHoraInicio.format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

                System.out.print("Confirmar? (S/N): ");
                String confirmacao = scanner.nextLine().trim().toUpperCase();

                if (confirmacao.equals("S")) {
                    Agendamento ag = new Agendamento();
                    ag.setPaciente(pacienteLogado);
                    ag.setPosto(disponibilidade.getPosto());
                    ag.setProfissional(disponibilidade.getProfissional());
                    ag.setDataHoraInicio(dataHoraInicio);
                    ag.setStatus(StatusAgendamento.AGENDADA);
                    ag.setLaudo("Aguardando atendimento");

                    agendamentoBO.salvar(ag, servicoSelecionado.getDuracaoMinutos());
                    System.out.println("Agendamento realizado com sucesso!");
                } else if (confirmacao.equals("N")) {
                    System.out.println("Operação cancelada.");
                } else {
                    System.out.println("Opção inválida. Operação cancelada.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Erro de digitação: Por favor, digite um ID válido.");
        } catch (SQLException e) {
            System.out.println("Erro no sistema: " + e.getMessage());
        } catch (NegocioException e) {
            System.out.println(e.getMessage());
        }
    }

    private Disponibilidade escolherDisponibilidade(Especialidade esp) throws SQLException, NegocioException {
        System.out.println("\n=== HORÁRIOS E LOCAIS DISPONÍVEIS ===");

        List<Disponibilidade> lista = disponibilidadeBO.buscarPorEspecialidade(esp);

        if (lista.isEmpty()) {
            System.out.println("Não há profissionais com agenda para esta especialidade.");
            return null;
        }

        for (Disponibilidade d : lista) {
            System.out.printf("[%d] - %s | Dr. %s | %s | %s às %s%n",
                    d.getId(),
                    d.getPosto().getNome(),
                    d.getProfissional().getNome(),
                    traduzirDiaSemana(d.getDiaSemana()),
                    d.getHoraInicio(),
                    d.getHoraFim());
        }

        try {
            System.out.print("> Escolha o ID da agenda: ");
            int id = Integer.parseInt(scanner.nextLine());
            return disponibilidadeBO.buscarPorId(id);
        } catch (NumberFormatException e) {
            System.out.println("Erro de digitação: Por favor, digite um ID válido.");
            return null;
        }
    }

    private LocalDate calcularProximaData(java.time.DayOfWeek diaSemanaAlvo) {
        LocalDate data = LocalDate.now();
        while (data.getDayOfWeek() != diaSemanaAlvo) {
            data = data.plusDays(1);
        }
        return data;
    }
    
    private void exibirAgendamentos() throws NegocioException {
        System.out.println("\n=== MEUS AGENDAMENTOS ===");

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            List<Agendamento> agendamentosDoPaciente =
                    agendamentoBO.buscarAgendamentosPorPaciente(pacienteLogado.getId());

            if (agendamentosDoPaciente.isEmpty()) {
                System.out.println("Você não possui agendamentos.");
                return;
            }

            for (Agendamento ag : agendamentosDoPaciente) {
                LocalDate data = ag.getDataHoraInicio().toLocalDate();
                LocalTime horaInicio = ag.getDataHoraInicio().toLocalTime();
                LocalTime horaFim = ag.getDataHoraFim().toLocalTime();

                System.out.printf(
                        "[%d] - Profissional: Dr. %s | Posto: %s | Data: %s | Horário: %s às %s | Status: %s | Laudo: %s%n",
                        ag.getId(),
                        ag.getProfissional().getNome(),
                        ag.getPosto().getNome(),
                        data.format(formato),
                        horaInicio,
                        horaFim,
                        ag.getStatus().toString(),
                        ag.getLaudo());
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar agendamentos: " + e.getMessage());
        }
    }

    private void mostrarDados() {
        System.out.println("\n--- MEUS DADOS ---");
        System.out.println("Nome: " + pacienteLogado.getNome());
        System.out.println("CPF: " + pacienteLogado.getCpf());
        System.out.println("SUS: " + pacienteLogado.getCartaoSus());
        System.out.println("Telefone: " + pacienteLogado.getTelefone());
        System.out.println("Endereço: " + pacienteLogado.getEndereco());
    }

    private String traduzirDiaSemana(java.time.DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "Segunda-feira";
            case TUESDAY -> "Terça-feira";
            case WEDNESDAY -> "Quarta-feira";
            case THURSDAY -> "Quinta-feira";
            case FRIDAY -> "Sexta-feira";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }
}
