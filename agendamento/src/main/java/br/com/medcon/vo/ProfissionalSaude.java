package br.com.medcon.vo;
import java.time.LocalDate;

import br.com.medcon.enums.CargoProfissional;

public class ProfissionalSaude extends Pessoa {
    private String registroProfissional;
    private CargoProfissional tipo; 
    private Especialidade especialidade;
    
    public ProfissionalSaude() {
        super();
    }
    public ProfissionalSaude(int id, String nome, String cpf, LocalDate dataNascimento, 
                             String telefone, String endereco, String registroProfissional, 
                             CargoProfissional tipo, Especialidade especialidade) {
        super(id, nome, cpf, dataNascimento, telefone, endereco);
        this.registroProfissional = registroProfissional;
        this.tipo = tipo;
        this.especialidade = especialidade;
    }
    public String getRegistroProfissional() { return registroProfissional; }
    public void setRegistroProfissional(String registroProfissional) { this.registroProfissional = registroProfissional; }

    public CargoProfissional getTipo() { return tipo; }
    public void setTipo(CargoProfissional tipo) { this.tipo = tipo; }
    
    public Especialidade getEspecialidade() { return especialidade; }
    public void setEspecialidade(Especialidade especialidade) { this.especialidade = especialidade; }
}