package br.com.medcon.vo;

import java.time.LocalDate;

public class HistoricoMedico {
    
    private int id;
    private Paciente paciente; 
    private LocalDate dataRegistro;
    private String tipoEvento; 
    private String detalhes; 

    public HistoricoMedico() {}
    public HistoricoMedico(int id, Paciente paciente, LocalDate dataRegistro, 
                           String tipoEvento, String detalhes) {
        this.id = id;
        this.paciente = paciente;
        this.dataRegistro = dataRegistro;
        this.tipoEvento = tipoEvento;
        this.detalhes = detalhes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }
    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
    public String getDetalhes() { return detalhes; }
    public void setDetalhes(String detalhes) { this.detalhes = detalhes; }
}