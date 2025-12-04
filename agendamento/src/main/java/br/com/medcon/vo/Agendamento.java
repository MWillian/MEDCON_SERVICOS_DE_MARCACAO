package br.com.medcon.vo;

import java.time.LocalDateTime;

import br.com.medcon.enums.StatusAgendamento;

public class Agendamento {
    
    private int id;
    private Solicitacao solicitacao; 
    private ProfissionalSaude profissional;
    private PostoSaude posto; 
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim; 
    private StatusAgendamento status;
    private String laudo;

    public Agendamento() {}
    public Agendamento(int id, Solicitacao solicitacao, ProfissionalSaude profissional, 
                       PostoSaude posto, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, 
                       StatusAgendamento status, String laudo) {
        this.id = id;
        this.solicitacao = solicitacao;
        this.profissional = profissional;
        this.posto = posto;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.status = status;
        this.laudo = laudo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Solicitacao getSolicitacao() { return solicitacao; }
    public void setSolicitacao(Solicitacao solicitacao) { this.solicitacao = solicitacao; }
    public ProfissionalSaude getProfissional() { return profissional; }
    public void setProfissional(ProfissionalSaude profissional) { this.profissional = profissional; }
    public PostoSaude getPosto() { return posto; }
    public void setPosto(PostoSaude posto) { this.posto = posto; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }
    public LocalDateTime getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(LocalDateTime dataHoraFim) { this.dataHoraFim = dataHoraFim; }
    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }
    public String getLaudo() { return laudo; }
    public void setLaudo(String laudo) { this.laudo = laudo; }
}