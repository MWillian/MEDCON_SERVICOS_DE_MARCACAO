package br.com.medcon.view;
import br.com.medcon.vo.*;
import br.com.medcon.bo.*;
import br.com.medcon.bo.exception.*;

import br.com.medcon.bo.AgendamentoBO;
import br.com.medcon.bo.DisponibilidadeBO;
import br.com.medcon.bo.EspecialidadeBO;
import br.com.medcon.bo.PacienteBO;
import br.com.medcon.bo.ProfissionalPostoBO;
import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.DisponibilidadeDAO;
import br.com.medcon.enums.StatusAgendamento;
import br.com.medcon.vo.Agendamento;
import br.com.medcon.vo.Disponibilidade;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.Paciente;
import br.com.medcon.vo.PostoSaude;
import br.com.medcon.vo.ProfissionalPosto;
import br.com.medcon.vo.ProfissionalSaude;
import br.com.medcon.vo.TipoServico;
import br.com.medcon.bo.TipoServicoBO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MenuPacienteView {
    private final Scanner scanner;
    private final PacienteBO pacienteBO;
    private Paciente pacienteLogado;
    private final TipoServicoBO tipoServicoBO;
    private final EspecialidadeBO especialidadeBO;
    private final DisponibilidadeBO disponibilidadeBO;
    private final AgendamentoBO agendamentoBO;
    private final ProfissionalPostoBO profissionalPostoBO;

    public MenuPacienteView(
            Scanner scanner,
            PacienteBO pacienteBO,
            TipoServicoBO tipoServicoBO,
            EspecialidadeBO especialidadeBO,
            DisponibilidadeBO disponibilidadeBO,
            AgendamentoBO agendamentoBO,
            ProfissionalPostoBO profissionalPostoBO) {
        this.scanner = scanner;
        this.pacienteBO = pacienteBO;
        this.tipoServicoBO = tipoServicoBO;
        this.especialidadeBO = especialidadeBO;
        this.disponibilidadeBO = disponibilidadeBO;
        this.agendamentoBO = agendamentoBO;
        this.profissionalPostoBO = profissionalPostoBO;
    }

    public void iniciar() {
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
                    break;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    // ACESSO AO SISTEMA
    private void fazerLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Digite seu CPF (apenas números): ");
        String cpf = scanner.nextLine();
        try {
            Paciente p = pacienteBO.buscarPorCpf(cpf);
            if (p != null) {
                this.pacienteLogado = p;
                System.out.println("Bem-vindo de volta, " + p.getNome() + "!");
                exibirMenuLogado();
            } else {
                System.out.println("CPF não encontrado. Faça seu cadastro.");
            }
        } catch (SQLException e) {
            System.out.println("Erro no sistema: " + e.getMessage());
        }
    }

    private void cadastrarPaciente() {
        try {
            System.out.println("\n--- NOVO CADASTRO ---");
            System.out.print("Nome Completo: ");
            String nome = scanner.nextLine();
            System.out.print("CPF (XXX.XXX.XXX-XX): ");
            String cpf = scanner.nextLine();
            System.out.print("Data Nascimento (dd/MM/yyyy): ");
            String dataTexto = scanner.nextLine();
            LocalDate dataNasc = LocalDate.parse(dataTexto, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            System.out.print("Telefone: ");
            String fone = scanner.nextLine();
            System.out.print("Endereço: ");
            String endereco = scanner.nextLine();
            System.out.print("Cartão SUS: ");
            String cartaoSus = scanner.nextLine();
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
            e.printStackTrace(); // erro genérico no banco.
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage()); // erro genérico no sistema
        }
    }

    private void exibirMenuLogado() {
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
                case "3" -> mostrarDados();
                case "0" -> {
                    this.pacienteLogado = null; // Limpa sessão
                    System.out.println("Deslogado com sucesso.");
                    break; // Volta para o menu de Login
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void novaSolicitacao() {
        System.out.println("\n=== NOVO AGENDAMENTO: SELECIONE O SERVIÇO ===");
        try {
            List<TipoServico> servicos = tipoServicoBO.listarTodos();
            if (servicos.isEmpty()) {
                System.out.println("Nenhum serviço cadastrado no sistema no momento.");
                return;
            }
            for (TipoServico servico : servicos) {
                // Exibe: [1] - Cardiologia (30 min)
                System.out.printf("[%d] - Serviço: %s, Duração Média: (%d min)\n", servico.getId(), servico.getNome(),
                        servico.getDuracaoMinutos());
            }
            System.out.println("0 - Voltar");
            System.out.print("> Digite o ID do serviço desejado: ");
            String entrada = scanner.nextLine();
            int idEscolhido = Integer.parseInt(entrada);
            if (idEscolhido == 0)
                return;
            TipoServico servicoSelecionado = tipoServicoBO.buscarPorId(idEscolhido);
            if (servicoSelecionado != null) {
                System.out.println("Você selecionou: " + servicoSelecionado.getNome());
                Especialidade esp = escolherEspecialidades();
                if (esp != null) {
                    ProfissionalPosto pp = escolherProfissionalPosto();
                    PostoSaude posto = pp.getPosto();
                    ProfissionalSaude profPosto = pp.getProfissional();
                    System.out.println(profPosto);
                    // TO-DO: Concluir agendamento
                    Agendamento ag = new Agendamento();
                    ag.setPaciente(pacienteLogado);
                    ag.setPosto(posto);
                    ag.setProfissional(profPosto);
                    ag.setDataHoraInicio(LocalDateTime.now());
                    ag.setStatus(StatusAgendamento.AGENDADA);
                    ag.setLaudo("Laudo em breve");
                    
                    agendamentoBO.salvar(ag);
                }
            } else {
                System.out.println("Serviço não encontrado. Tente novamente.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Digite apenas números.");
        } catch (SQLException e) {
            System.out.println("Erro ao buscar serviços: " + e.getMessage());
        }
    }

    private Especialidade escolherEspecialidades() {
        System.out.println("\n=== SELECIONE A ESPECIALIDADE ===");
        Especialidade especialidadeSelecionada = null;
        try {
            List<Especialidade> especialidades = especialidadeBO.listarTodos();
            for (Especialidade especialidade : especialidades) {
                System.out.printf("[%d] - Especialidade: %s, Descrição: %s\n", especialidade.getId(),
                        especialidade.getNome(), especialidade.getDescricao());
            }
            System.out.println("Selecione a especialidade pelo [ID], ex.: 3");
            int id_selecionado = scanner.nextInt();
            especialidadeSelecionada = especialidadeBO.buscarPorId(id_selecionado);
        } catch (SQLException e) {
            System.out.println("Erro inesperado do banco: " + e.getMessage());
        } catch (NegocioException e) {
            System.out.println(e.getMessage());
        }
        return especialidadeSelecionada;
    }

    private ProfissionalPosto escolherProfissionalPosto () {
        System.out.println("\n=== POSTOS DISPONÍVES PARA ESSE SERVIÇO ===");
        ProfissionalPosto profissionalPosto = new ProfissionalPosto();
        try {
            List<Disponibilidade> postos = disponibilidadeBO.listarTodos();
            for (Disponibilidade dis : postos) {
                System.out.printf("[%d] - Nome: %s, Endereço: %s, Telefone: %s, Dia: %s, Horários[ Início: %s, Fim: %s ]\n", 
                dis.getPosto().getId(), dis.getPosto().getNome(), dis.getPosto().getEndereco(), dis.getPosto().getTelefone(), dis.getDiaSemana().toString(), dis.getHoraInicio().toString(), dis.getHoraFim().toString());
            }
            System.out.println("Selecione o Posto pelo [ID], ex.: 3");
            int id_selecionado = scanner.nextInt();
            profissionalPosto.setPosto(disponibilidadeBO.buscarPorId(id_selecionado).getPosto()); 
            profissionalPosto.setProfissional(disponibilidadeBO.buscarPorId(id_selecionado).getProfissional());
        } catch (SQLException e) {
            System.out.println("Erro inesperado do banco: " + e.getMessage());
        }  catch (NegocioException e) {
            System.out.println(e.getMessage());
        }
        return profissionalPosto;
    }

    private void mostrarDados() {
        System.out.println("\n--- MEUS DADOS ---");
        System.out.println("Nome: " + pacienteLogado.getNome());
        System.out.println("CPF: " + pacienteLogado.getCpf());
        System.out.println("SUS: " + pacienteLogado.getCartaoSus());
        System.out.println("Telefone: " + pacienteLogado.getTelefone());
    }
}
