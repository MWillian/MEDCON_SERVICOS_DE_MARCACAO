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
            System.out.println("1. Já tenho cadastro (Login) ");
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

    // ACESSO AO SISTEMA
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
        }  catch (NegocioException e) {
            System.out.println(e.getMessage());
        }
    }

    private void cadastrarPaciente() {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            System.out.println("\n--- NOVO CADASTRO ---");
            System.out.print("Nome Completo: ");
            String nome = scanner.nextLine();
            pacienteBO.ValidarNome(nome);

            System.out.print("CPF (XXX.XXX.XXX-XX): ");
            String cpf = scanner.nextLine();
            pacienteBO.ValidarCpf(pacienteBO.limparNumero(cpf));

            System.out.print("Data Nascimento (dd/MM/yyyy): ");
            String dataTexto = scanner.nextLine();
            pacienteBO.ValidarDataNascimento(LocalDate.parse(dataTexto, formatador));
            
            LocalDate dataNasc = LocalDate.parse(dataTexto, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            System.out.print("Telefone: ");
            String fone = scanner.nextLine();
            pacienteBO.ValidarTelefone(pacienteBO.limparNumero(fone));

            System.out.print("Endereço: ");
            String endereco = scanner.nextLine();
            pacienteBO.ValidarEndereco(endereco);

            System.out.print("Cartão SUS: ");
            String cartaoSus = scanner.nextLine();
            pacienteBO.ValidarCartaoSus(pacienteBO.limparNumero(cartaoSus));

            Paciente novoPaciente = new Paciente(0, nome, cpf, dataNasc, fone, endereco, cartaoSus);
            pacienteBO.salvar(novoPaciente);

            System.out.println("Cadastro realizado com sucesso! Bem-vindo(a), " + nome);
            this.pacienteLogado = novoPaciente;
            System.out.println("Cadastro realizado! Você está logado.");

            exibirMenuLogado();
        } catch (NegocioException e) {
            System.out.println("ALERTA: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Erro: Data inválida. Use o formato dd/MM/yyyy");
        } catch (SQLException e) {
            System.out.println("Erro interno no sistema. Tente novamente mais tarde.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage()); 
        }
    }

    private void exibirMenuLogado() throws NegocioException {
        while (pacienteLogado != null) {
            System.out.println("\n--- OLÁ, " + pacienteLogado.getNome().toUpperCase() + " ---");
            System.out.println("1. Nova Solicitação de Agendamento");
            System.out.println("2. Meus Agendamentos (Futuro)");
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
                    break;
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
                System.out.printf("[%d] - Serviço: %s, Duração Média: (%d min)\n", s.getId(), s.getNome(),s.getDuracaoMinutos());
            }

            System.out.println("0 - Voltar");
            System.out.print("> Digite o ID do serviço desejado: ");
            System.out.print("> Digite o ID do serviço: ");
            int idServico = Integer.parseInt(scanner.nextLine());
            TipoServico servicoSelecionado = tipoServicoBO.buscarPorId (idServico);
            
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
                System.out.println("Médico: " + disponibilidade.getProfissional().getNome());
                System.out.println("Local: " + disponibilidade.getPosto().getNome());
                System.out.println("Data: " + dataHoraInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                
                System.out.print("Confirmar? (S/N): ");
                if (scanner.nextLine().equalsIgnoreCase("S")) {
                    
                    Agendamento ag = new Agendamento();
                    ag.setPaciente(pacienteLogado);
                    ag.setPosto(disponibilidade.getPosto());
                    ag.setProfissional(disponibilidade.getProfissional());
                    ag.setDataHoraInicio(dataHoraInicio);
                    ag.setStatus(StatusAgendamento.AGENDADA);
                    ag.setLaudo("Aguardando atendimento");

                    agendamentoBO.salvar(ag, servicoSelecionado.getDuracaoMinutos());
                    System.out.println("Agendamento realizado com sucesso!");
                } else {
                    System.out.println("OPÇÃO INVÁLIDA: Operação cancelada.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Digite apenas números.");
        } catch (SQLException e) {
            System.out.println("Erro de banco: " + e.getMessage());
        } catch (NegocioException e) {
            System.out.println(e.getMessage());
        }
    }

    private Disponibilidade escolherDisponibilidade(Especialidade esp) throws SQLException, NegocioException {
        System.out.println("\n=== HORÁRIOS E LOCAIS DISPONÍVEIS ===");
        
        List<Disponibilidade> lista = disponibilidadeBO.buscarPorEspecialidade(esp);
        
        if (lista.isEmpty()) {
            System.out.println("Não há médicos com agenda para esta especialidade.");
            return null;
        }

        for (Disponibilidade d : lista) {
            System.out.printf("[%d] - %s | Dr. %s | %s | %s às %s\n",
                d.getId(),
                d.getPosto().getNome(),
                d.getProfissional().getNome(),
                traduzirDiaSemana(d.getDiaSemana()),
                d.getHoraInicio(),
                d.getHoraFim()
            );
        }
        
        System.out.print("> Escolha o ID da agenda: ");
        int id = Integer.parseInt(scanner.nextLine());
        return disponibilidadeBO.buscarPorId(id);
    }

    private LocalDate calcularProximaData(java.time.DayOfWeek diaSemanaAlvo) {
        LocalDate data = LocalDate.now();
        while (data.getDayOfWeek() != diaSemanaAlvo) {
            data = data.plusDays(1);
        }
        return data;
    }

    private void exibirAgendamentos() throws NegocioException{
        System.out.println("\n=== AGENDAMENTOS ===");

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Agendamento> agendamentosDoPaciente;
        try {
            agendamentosDoPaciente = agendamentoBO.buscarAgendamentosPorPaciente(pacienteLogado.getId());
            
            for (Agendamento ag : agendamentosDoPaciente) {
                LocalDate data = ag.getDataHoraInicio().toLocalDate();
                LocalTime hora_incio = ag.getDataHoraInicio().toLocalTime();
                LocalTime hora_fim = ag.getDataHoraFim().toLocalTime();

                System.out.printf("[%d] - Paciente: %s | Médico: Dr. %s | Posto: %s | Data: %s | Horário: %s às %s | Status: %s | Laudo: %s\n",
                    ag.getId(),
                    ag.getPaciente().getNome(),
                    ag.getProfissional().getNome(),
                    ag.getPosto().getNome(),
                    //ag.getPosto().getEndereco(),
                    //ag.getPosto().getTelefone(),
                    data.format(formato),
                    hora_incio,
                    hora_fim,
                    ag.getStatus().toString(),
                    ag.getLaudo()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NegocioException e) {
            System.out.println(e.getMessage());
        }
        
    }

    private void mostrarDados() {
        System.out.println("\n--- MEUS DADOS ---");
        System.out.println("Nome: " + pacienteLogado.getNome());
        System.out.println("CPF: " + pacienteLogado.getCpf());
        System.out.println("SUS: " + pacienteLogado.getCartaoSus());
        System.out.println("Telefone: " + pacienteLogado.getTelefone());
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
