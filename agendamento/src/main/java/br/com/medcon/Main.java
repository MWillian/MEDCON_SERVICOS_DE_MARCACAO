package br.com.medcon;

import java.util.Scanner;

import br.com.medcon.bo.AgendamentoBO;
import br.com.medcon.bo.DisponibilidadeBO;
import br.com.medcon.bo.EspecialidadeBO;
import br.com.medcon.bo.PacienteBO;
import br.com.medcon.bo.PostoSaudeBO;
import br.com.medcon.bo.ProfissionalPostoBO;
import br.com.medcon.bo.TipoServicoBO;
import br.com.medcon.dao.AgendamentoDAO;
import br.com.medcon.dao.DisponibilidadeDAO;
import br.com.medcon.dao.EspecialidadeDAO;
import br.com.medcon.dao.PacienteDAO;
import br.com.medcon.dao.PostoSaudeDAO;
import br.com.medcon.dao.ProfissionalPostoDAO;
import br.com.medcon.dao.TipoServicoDAO;
import br.com.medcon.view.MenuAdminView;
import br.com.medcon.view.MenuPacienteView;
;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            PacienteDAO pacienteDAO = new PacienteDAO();
            TipoServicoDAO tipoServicoDAO = new TipoServicoDAO();
            EspecialidadeDAO especialidadeDAO = new EspecialidadeDAO();
            DisponibilidadeDAO disponibilidadeDAO = new DisponibilidadeDAO();
            AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
            ProfissionalPostoDAO profissionalPostoDAO = new ProfissionalPostoDAO();
            PostoSaudeDAO PostoDAO = new PostoSaudeDAO();

            PacienteBO pacienteBO = new PacienteBO(pacienteDAO);
            TipoServicoBO tipoServicoBO = new TipoServicoBO(tipoServicoDAO, especialidadeDAO);
            EspecialidadeBO especialidadeBO = new EspecialidadeBO(especialidadeDAO);
            DisponibilidadeBO disponibilidadeBO = new DisponibilidadeBO(disponibilidadeDAO);
            AgendamentoBO agendamentoBO = new AgendamentoBO(agendamentoDAO);
            ProfissionalPostoBO profissionalPostoBO = new ProfissionalPostoBO(profissionalPostoDAO);
            PostoSaudeBO postoSaudeBO = new PostoSaudeBO(PostoDAO);

            MenuPacienteView menuPaciente = new MenuPacienteView(
                scanner, 
                pacienteBO, 
                tipoServicoBO, 
                especialidadeBO, 
                disponibilidadeBO, 
                agendamentoBO,
                profissionalPostoBO,
                postoSaudeBO
            );
            
            MenuAdminView menuAdmin = new MenuAdminView(scanner, tipoServicoBO );

            boolean rodando = true;
            while (rodando) {
                System.out.println("\n=== SISTEMA MEDCON ===");
                System.out.println("Selecione seu perfil:");
                System.out.println("1. Paciente");
                System.out.println("2. Administrador");
                System.out.println("0. Sair");
                System.out.print("> ");
                
                String perfil = scanner.nextLine();
                
                switch (perfil) {
                    case "1" -> menuPaciente.iniciar(); 
                    case "2" -> menuAdmin.iniciar();    
                    case "0" -> {
                        System.out.println("Encerrando sistema...");
                        rodando = false;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro fatal no sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}