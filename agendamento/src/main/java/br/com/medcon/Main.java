package br.com.medcon;
import br.com.medcon.view.MenuPacienteView;

import java.util.Scanner;

import br.com.medcon.bo.*; 
import br.com.medcon.dao.*;
import br.com.medcon.view.*;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            PacienteDAO pacienteDAO = new PacienteDAO();
            TipoServicoDAO tipoServicoDAO = new TipoServicoDAO();
            EspecialidadeDAO especialidadeDAO = new EspecialidadeDAO();
            DisponibilidadeDAO disponibilidadeDAO = new DisponibilidadeDAO();

            PacienteBO pacienteBO = new PacienteBO(pacienteDAO);
            TipoServicoBO tipoServicoBO = new TipoServicoBO(tipoServicoDAO,especialidadeDAO); 
            EspecialidadeBO especialidadeBO = new EspecialidadeBO(especialidadeDAO);
            DisponibilidadeBO disponibilidadeBO = new DisponibilidadeBO(disponibilidadeDAO);

            MenuPacienteView menuPaciente = new MenuPacienteView(scanner, pacienteBO, tipoServicoBO, especialidadeBO, disponibilidadeBO);
            MenuAdminView MenuAdmin = new MenuAdminView(scanner, tipoServicoBO);
            System.out.println("=== SISTEMA MEDCON ===");
            System.out.println("Selecione seu perfil:");
            System.out.println("1. Paciente");
            System.out.println("2. Administrador");
            System.out.print("> ");
            String perfil = scanner.nextLine();
            switch (perfil) {
                case "1" -> menuPaciente.iniciar();
                case "2" -> MenuAdmin.iniciar();
                default -> System.out.println("Menu de Admin ainda não implementado.");
            }
        }
    }
}