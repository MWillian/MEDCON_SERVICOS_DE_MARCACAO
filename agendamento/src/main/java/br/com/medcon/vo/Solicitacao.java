package br.com.medcon.vo;
import java.time.LocalDateTime;

import br.com.medcon.enums.Prioridade;
import br.com.medcon.enums.StatusSolicitacao;

public class Solicitacao {
    
    private int id;
    private Paciente paciente;
    private TipoServico tipoServico; 
    private LocalDateTime dataSolicitacao;
    private Prioridade prioridade;
    private StatusSolicitacao status;
    public Solicitacao() {}
    public Solicitacao(int id, Paciente paciente, TipoServico tipoServico, 
                       LocalDateTime dataSolicitacao, Prioridade prioridade, 
                       StatusSolicitacao status) {
        this.id = id;
        this.paciente = paciente;
        this.tipoServico = tipoServico;
        this.dataSolicitacao = dataSolicitacao;
        this.prioridade = prioridade;
        this.status = status;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public TipoServico getTipoServico() { return tipoServico; }
    public void setTipoServico(TipoServico tipoServico) { this.tipoServico = tipoServico; }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public Prioridade getPrioridade() { return prioridade; }
    public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }
    
    public StatusSolicitacao getStatus() { return status; }
    public void setStatus(StatusSolicitacao status) { this.status = status; }
}