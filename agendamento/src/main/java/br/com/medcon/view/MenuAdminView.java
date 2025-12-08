package br.com.medcon.view;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import br.com.medcon.bo.DisponibilidadeBO;
import br.com.medcon.bo.EspecialidadeBO;
import br.com.medcon.bo.PostoSaudeBO;
import br.com.medcon.bo.ProfissionalSaudeBO;
import br.com.medcon.bo.TipoServicoBO;
import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.enums.CargoProfissional;
import br.com.medcon.vo.Disponibilidade;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.PostoSaude;
import br.com.medcon.vo.ProfissionalSaude;
import br.com.medcon.vo.TipoServico;


public class MenuAdminView {
    private final Scanner scanner;
    private final TipoServicoBO tipoServicoBO;
    private final EspecialidadeBO especialidadeBO;
    private final PostoSaudeBO postoSaudeBO;
    private final ProfissionalSaudeBO profissionalSaudeBO;
    private final DisponibilidadeBO disponibilidadeBO;

    public MenuAdminView(Scanner scanner, 
                     TipoServicoBO tipoServicoBO, 
                     EspecialidadeBO especialidadeBO,
                     PostoSaudeBO postoSaudeBO,
                     ProfissionalSaudeBO profissionalSaudeBO,
                     DisponibilidadeBO disponibilidadeBO) {
        this.scanner = scanner;
        this.tipoServicoBO = tipoServicoBO;
        this.especialidadeBO = especialidadeBO;
        this.postoSaudeBO = postoSaudeBO;
        this.profissionalSaudeBO = profissionalSaudeBO;
        this.disponibilidadeBO = disponibilidadeBO;
    }

    public void iniciar() {
        while (true) {
            System.out.println("\n=== ÁREA ADMINISTRATIVA ===");
            System.out.println("1. Gerenciar Especialidades");
            System.out.println("2. Gerenciar Serviços");
            System.out.println("3. Gerenciar Postos de Saúde");
            System.out.println("4. Gerenciar Profissionais");
            System.out.println("5. Cadastrar Grade Horária (Disponibilidade)");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("> Opção: ");

            String opcao = scanner.nextLine();

            try {
                switch (opcao) {
                    case "1" -> menuEspecialidade();
                    case "2" -> menuServico();
                    case "3" -> menuPosto();
                    case "4" -> menuProfissional();
                    case "5" -> cadastrarDisponibilidade();
                    case "0" -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (NegocioException | SQLException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }
    
    private void menuEspecialidade() throws SQLException, NegocioException {
        System.out.println("\n--- ESPECIALIDADES ---");
        System.out.println("1. Listar | 2. Cadastrar nova especialidade");
        String op = scanner.nextLine();
        
        if (op.equals("1")) {
            especialidadeBO.listarTodos().forEach(e -> 
                System.out.printf("[%d] %s - %s\n", e.getId(), e.getNome(), e.getDescricao()));
        } else if (op.equals("2")) {
            System.out.print("Nome: "); String nome = scanner.nextLine();
            System.out.print("Descrição: "); String desc = scanner.nextLine();

            Especialidade e = new Especialidade(0, nome, desc); 
            especialidadeBO.salvar(e);
            System.out.println("Especialidade salva!");
        }
    }

    private void menuServico() throws SQLException, NegocioException {
        System.out.println("\n--- TIPOS DE SERVIÇO ---");
        System.out.println("1. Listar | 2. Cadastrar");
        String op = scanner.nextLine();

        if (op.equals("1")) {
            tipoServicoBO.listarTodos().forEach(s -> 
                System.out.printf("[%d] %s (%d min) - Req: %s\n", s.getId(), s.getNome(), s.getDuracaoMinutos(), s.getEspecialidadeNecessaria().getNome()));
        } else if (op.equals("2")) {
            System.out.print("Nome do Serviço: "); String nome = scanner.nextLine();
            System.out.print("Duração (min): "); int duracao = Integer.parseInt(scanner.nextLine());

            System.out.println("Selecione a Especialidade Necessária:");
            especialidadeBO.listarTodos().forEach(e -> System.out.printf("[%d] %s\n", e.getId(), e.getNome()));
            System.out.print("> ID: "); 
            int idEsp = Integer.parseInt(scanner.nextLine());
            
            Especialidade esp = new Especialidade(); 
            esp.setId(idEsp);

            TipoServico ts = new TipoServico(0, nome, duracao, esp);
            tipoServicoBO.salvar(ts);
            System.out.println("Serviço salvo!");
        }
    }

    private void menuPosto() throws SQLException, NegocioException {
        System.out.println("\n--- POSTOS DE SAÚDE ---");
        System.out.println("1. Listar | 2. Cadastrar");
        String op = scanner.nextLine();

        if (op.equals("1")) {
            postoSaudeBO.listarTodos().forEach(p -> 
                System.out.printf("[%d] %s - %s\n", p.getId(), p.getNome(), p.getEndereco()));
        } else if (op.equals("2")) {
            System.out.print("Nome: "); String nome = scanner.nextLine();
            System.out.print("Endereço: "); String end = scanner.nextLine();
            System.out.print("Telefone: "); String tel = scanner.nextLine();

            PostoSaude p = new PostoSaude(0, nome, end, tel);
            postoSaudeBO.salvar(p);
            System.out.println("Posto salvo!");
        }
    }

    private void menuProfissional() throws SQLException, NegocioException {
        System.out.println("\n--- PROFISSIONAIS ---");
        System.out.println("1. Listar | 2. Cadastrar");
        String op = scanner.nextLine();

        if (op.equals("1")) {
            profissionalSaudeBO.listarTodos().forEach(p -> 
                System.out.printf("[%d] %s (%s) - %s\n", p.getId(), p.getNome(), p.getTipo(), p.getEspecialidade().getNome()));
        } else if (op.equals("2")) {
            ProfissionalSaude p = new ProfissionalSaude();

            System.out.print("Nome: "); 
            p.setNome(scanner.nextLine());

            System.out.print("CPF: "); 
            p.setCpf(scanner.nextLine());

            System.out.println("Data de Nascimento (dd/MM/yyyy): "); 
            String dataTexto = scanner.nextLine();
            LocalDate dataNascimento = LocalDate.parse(dataTexto,DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            p.setDataNascimento(dataNascimento);

            System.out.print("Registro Profissional: "); 
            p.setRegistroProfissional(scanner.nextLine());

            System.out.print("Telefone: "); 
            p.setTelefone(scanner.nextLine());

            System.out.print("Endereço: "); 
            p.setEndereco(scanner.nextLine());

            System.out.println("Cargo (1-MEDICO, 2-ENFERMEIRO, 3-TECNICO): ");

            int cargo = Integer.parseInt(scanner.nextLine());
            p.setTipo(cargo == 1 ? CargoProfissional.MEDICO : cargo == 2 ? CargoProfissional.ENFERMEIRO : CargoProfissional.TECNICO);

            System.out.println("Especialidade (ID): ");
            especialidadeBO.listarTodos().forEach(e -> System.out.printf("[%d] %s\n", e.getId(), e.getNome()));
            int idEsp = Integer.parseInt(scanner.nextLine());
            
            Especialidade e = new Especialidade();
            e.setId(idEsp);
            p.setEspecialidade(e);

            profissionalSaudeBO.salvar(p);
            System.out.println("Profissional salvo!");
        }
    }

    private void cadastrarDisponibilidade() throws SQLException, NegocioException {
        System.out.println("\n--- NOVA GRADE HORÁRIA ---");
        
        System.out.println("Selecione o Profissional:");
        profissionalSaudeBO.listarTodos().forEach(p -> System.out.printf("[%d] %s\n", p.getId(), p.getNome()));
        System.out.print("> ID: "); 
        int idProf = Integer.parseInt(scanner.nextLine());
        ProfissionalSaude prof = profissionalSaudeBO.buscarPorId(idProf);

        System.out.println("Selecione o Posto:");
        postoSaudeBO.listarTodos().forEach(p -> System.out.printf("[%d] %s\n", p.getId(), p.getNome()));
        System.out.print("> ID: "); 
        int idPosto = Integer.parseInt(scanner.nextLine());
        PostoSaude posto = postoSaudeBO.buscarPorId(idPosto);

        System.out.println("Dia da Semana (1-SEGUNDA ... 7-DOMINGO): ");
        int dia = Integer.parseInt(scanner.nextLine());
        DayOfWeek diaSemana = DayOfWeek.of(dia); 

        System.out.print("Hora Início (HH:mm): ");
        LocalTime inicio = LocalTime.parse(scanner.nextLine());

        System.out.print("Hora Fim (HH:mm): ");
        LocalTime fim = LocalTime.parse(scanner.nextLine());

        Disponibilidade d = new Disponibilidade();
        d.setProfissional(prof);
        d.setPosto(posto);
        d.setDiaSemana(diaSemana);
        d.setHoraInicio(inicio);
        d.setHoraFim(fim);

        disponibilidadeBO.salvar(d);
        System.out.println("Grade cadastrada com sucesso!");
    }
}
