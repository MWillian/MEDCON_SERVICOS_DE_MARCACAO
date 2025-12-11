package br.com.medcon.view;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    public MenuAdminView(
            Scanner scanner,
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
        if (!fazerLoginAdmin()) {
            return; 
        }
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
                System.out.printf("[%d] %s - %s%n", e.getId(), e.getNome(), e.getDescricao()));
        } else if (op.equals("2")) {
            System.out.print("Nome: ");
            String nome = scanner.nextLine();
            System.out.print("Descrição: ");
            String desc = scanner.nextLine();

            Especialidade e = new Especialidade(0, nome, desc); 
            especialidadeBO.salvar(e);
            System.out.println("Especialidade salva!");
        }
    }

    private boolean fazerLoginAdmin() {
        System.out.println("\n--- LOGIN ADMINISTRADOR ---");
        System.out.print("Digite seu CPF (apenas números): ");
        String cpf = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine(); 

        if (cpf.equals("12345678900") && senha.equals("admin123")) {
            System.out.println("Administrador logado com sucesso.");
            return true;
        }
        
        System.out.println("Credenciais de administrador inválidas.");
        return false;
    }

    private void menuServico() throws SQLException, NegocioException {
        System.out.println("\n--- TIPOS DE SERVIÇO ---");
        System.out.println("1. Listar | 2. Cadastrar");
        String op = scanner.nextLine();

        if (op.equals("1")) {
            tipoServicoBO.listarTodos().forEach(s ->
                    System.out.printf(
                            "[%d] %s (Duração Média: %d min) - Especialidade: %s%n",
                            s.getId(),
                            s.getNome(),
                            s.getDuracaoMinutos(),
                            s.getEspecialidadeNecessaria().getNome()
                    ));
        } else if (op.equals("2")) {
            try {
                System.out.print("Nome do Serviço: ");
                String nome = scanner.nextLine();

                System.out.print("Duração (min): ");
                int duracao = Integer.parseInt(scanner.nextLine());

                System.out.println("Selecione a Especialidade Necessária:");
                especialidadeBO.listarTodos().forEach(e ->
                        System.out.printf("[%d] %s%n", e.getId(), e.getNome()));

                System.out.print("> ID: ");
                int idEsp = Integer.parseInt(scanner.nextLine());

                Especialidade esp = new Especialidade();
                esp.setId(idEsp);

                TipoServico ts = new TipoServico(0, nome, duracao, esp);
                tipoServicoBO.salvar(ts);
                System.out.println("Serviço salvo!");
            } catch (NumberFormatException e) {
                System.out.println("Erro de digitação: Por favor, digite um número inteiro válido.");
            }
        }
    }

    private void menuPosto() throws SQLException, NegocioException {
        System.out.println("\n--- POSTOS DE SAÚDE ---");
        System.out.println("1. Listar | 2. Cadastrar");
        String op = scanner.nextLine();

        if (op.equals("1")) {
            postoSaudeBO.listarTodos().forEach(p ->
                System.out.printf("[%d] %s - %s%n", p.getId(), p.getNome(), p.getEndereco()));
        } else if (op.equals("2")) {
            System.out.print("Nome: ");
            String nome = scanner.nextLine();
            System.out.print("Endereço: ");
            String end = scanner.nextLine();
            System.out.print("Telefone: ");
            String tel = scanner.nextLine();

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
                    System.out.printf("[%d] %s (%s) - %s%n",
                            p.getId(),
                            p.getNome(),
                            p.getTipo(),
                            p.getEspecialidade().getNome()));
        } else if (op.equals("2")) {
            ProfissionalSaude p = new ProfissionalSaude();

            System.out.print("Nome: "); 
            p.setNome(scanner.nextLine());

            System.out.print("CPF: "); 
            p.setCpf(scanner.nextLine());

            System.out.println("Data de Nascimento (dd/MM/yyyy): ");
            String dataTexto = scanner.nextLine();
            try {
                LocalDate dataNascimento = LocalDate.parse(
                        dataTexto,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                );
                p.setDataNascimento(dataNascimento);
            } catch (DateTimeParseException e) {
                System.out.println("Erro: Data inválida. Use o formato dd/MM/yyyy.");
                return;
            }

            System.out.print("Registro Profissional: ");
            p.setRegistroProfissional(scanner.nextLine());

            System.out.print("Telefone: ");
            p.setTelefone(scanner.nextLine());

            System.out.print("Endereço: ");
            p.setEndereco(scanner.nextLine());

            System.out.println("Cargo (1-MEDICO, 2-ENFERMEIRO, 3-TECNICO): ");
            int cargo = Integer.parseInt(scanner.nextLine());
            p.setTipo(
                    cargo == 1 ? CargoProfissional.MEDICO
                            : cargo == 2 ? CargoProfissional.ENFERMEIRO
                            : CargoProfissional.TECNICO
            );

            System.out.println("Especialidade (ID): ");
            especialidadeBO.listarTodos().forEach(e ->
                    System.out.printf("[%d] %s%n", e.getId(), e.getNome()));

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

        try {
            System.out.println("\nSelecione o Profissional:");
            profissionalSaudeBO.listarTodos().forEach(p ->
                    System.out.printf("[%d] %s%n", p.getId(), p.getNome()));
            System.out.println("[0] - Voltar");
            System.out.print("> ID: ");
            String entradaProf = scanner.nextLine();
            if (entradaProf.equals("0")) return;

            int idProf = Integer.parseInt(entradaProf);
            ProfissionalSaude prof = profissionalSaudeBO.buscarPorId(idProf);

            System.out.println("\nSelecione o Posto:");
            postoSaudeBO.listarTodos().forEach(p ->
                    System.out.printf("[%d] %s%n", p.getId(), p.getNome()));
            System.out.println("[0] - Voltar");
            System.out.print("> ID: ");
            String entradaPosto = scanner.nextLine();
            if (entradaPosto.equals("0")) return;

            int idPosto = Integer.parseInt(entradaPosto);
            PostoSaude posto = postoSaudeBO.buscarPorId(idPosto);

            System.out.println("\nSelecione o Dia da Semana:");
            for (int i = 1; i <= 7; i++) {
                DayOfWeek dia = DayOfWeek.of(i);
                System.out.printf("[%d] - %s%n", i, traduzirDiaSemana(dia));
            }

            System.out.println("[0] - Voltar");
            System.out.print("> Opção (1-7): ");
            String entradaDia = scanner.nextLine();
            if (entradaDia.equals("0")) return;

            int dia = Integer.parseInt(entradaDia);
            if (dia < 1 || dia > 7) {
                throw new NegocioException("Dia da semana inválido. Use 1 a 7.");
            }
            DayOfWeek diaSemana = DayOfWeek.of(dia);

            System.out.print("Hora Início (HH:mm, ex: 08:00): ");
            LocalTime inicio = LocalTime.parse(scanner.nextLine());

            System.out.print("Hora Fim (HH:mm, ex: 12:00): ");
            LocalTime fim = LocalTime.parse(scanner.nextLine());

            Disponibilidade d = new Disponibilidade();
            d.setProfissional(prof);
            d.setPosto(posto);
            d.setDiaSemana(diaSemana);
            d.setHoraInicio(inicio);
            d.setHoraFim(fim);

            disponibilidadeBO.salvar(d);
            System.out.println("Grade cadastrada com sucesso!");

        } catch (NumberFormatException e) {
            System.out.println("Erro de digitação: Por favor, digite um ID válido.");
        } catch (DateTimeParseException e) {
            System.out.println("Erro de formato de hora. Use o formato HH:mm (ex: 08:00).");
        }
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
