package br.com.medcon.vo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Paciente extends Pessoa {
    private String cartaoSus; 
    private List<String> historicoMedico = new ArrayList<>(); 
    public Paciente(){
        super();
    }
    public Paciente(String cartaosus) {
        super();
        this.cartaoSus = cartaosus; 
    }
    public Paciente(int id, String nome, String cpf, LocalDate dataNascimento, String telefone, String endereco, String cartaoSus) {
        super(id, nome, cpf, dataNascimento, telefone, endereco);
        this.cartaoSus = cartaoSus;
    }
    public String getCartaoSus() { return cartaoSus; }
    public void setCartaoSus(String cartaoSus) { this.cartaoSus = cartaoSus; }

    public List<String> getHistoricoMedico() { return historicoMedico; }
    public void setHistoricoMedico(List<String> historicoMedico) { this.historicoMedico = historicoMedico; }
    
    public void adicionarHistorico(String registro) {
    this.historicoMedico.add(registro);
    }
}