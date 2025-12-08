package br.com.medcon.view;
import java.sql.SQLException;
import java.util.Scanner;

import br.com.medcon.bo.TipoServicoBO;

public class MenuAdminView {
    private final Scanner scanner;
    private final TipoServicoBO tipoServicoBO;
    public MenuAdminView(Scanner scanner, TipoServicoBO tipoServicoBO){
        this.scanner = scanner;
        this.tipoServicoBO = tipoServicoBO;
    }

    public void iniciar() {
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n=== ÁREA ADMINISTRATIVA ===");
            System.out.println("1. Gerenciar Postos de Saúde");
            System.out.println("2. Gerenciar Profissionais");
            System.out.println("3. Gerenciar Especialidades e Serviços");
            System.out.println("4. Gerenciar Grade Horária");
            System.out.println("5. Processar Fila de Agendamentos");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("> Opção: ");
            String opcao = scanner.nextLine();
            try {
                switch (opcao) {
                    case "1" -> gerenciarPostos();
                    case "2" -> gerenciarProfissionais();
                    case "3" -> gerenciarServicos();
                    case "0" -> rodando = false;
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Ocorreu um erro na operação: " + e.getMessage());
            }
        }
    }

    private void gerenciarPostos() {
        System.out.println("\n--- GERENCIAR POSTOS ---");
    }

    private void gerenciarProfissionais() {
        System.out.println("\n--- GERENCIAR PROFISSIONAIS ---");
    }

    private void gerenciarServicos() throws SQLException {
        System.out.println("\n--- SERVIÇOS E ESPECIALIDADES ---");
        System.out.println("1. Listar Tipos de Serviço");
        System.out.println("2. Cadastrar Novo Serviço");
        System.out.print("> Opção: ");
        String opcao = scanner.nextLine();
        if (opcao.equals("1")) {
            System.out.println("\n--- SERVIÇOS CADASTRADOS ---");
            tipoServicoBO.listarTodos().forEach(s -> {
                System.out.printf("ID %d: %s (%d min) - Especialidade ID %d\n", 
                    s.getId(), s.getNome(), s.getDuracaoMinutos(), s.getEspecialidadeNecessaria().getId());
            });
        }else if(opcao.equals("2")){
            System.out.println("\n--- CADASTRAR NOVO SERVIÇO ---");
        }
    }

}
