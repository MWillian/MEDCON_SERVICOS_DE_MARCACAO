package br.com.medcon.vo;
import java.time.LocalTime;
import java.time.DayOfWeek;

public class Disponibilidade {
    private int id;
    private ProfissionalSaude profissional;
    private PostoSaude posto;
    private DayOfWeek diaSemana; 
    private LocalTime horaInicio;
    private LocalTime horaFim; 
    public Disponibilidade() {}
    public Disponibilidade(int id, ProfissionalSaude profissional, PostoSaude posto, 
                           DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        this.id = id;
        this.profissional = profissional;
        this.posto = posto;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public ProfissionalSaude getProfissional() { return profissional; }
    public void setProfissional(ProfissionalSaude profissional) { this.profissional = profissional; }
    public PostoSaude getPosto() { return posto; }
    public void setPosto(PostoSaude posto) { this.posto = posto; }
    public DayOfWeek getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DayOfWeek diaSemana) { this.diaSemana = diaSemana; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    @Override
    public String toString() {
        return "Disponibilidade [id=" + id + ", profissional=" + profissional + ", posto=" + posto + ", diaSemana="
                + diaSemana + ", horaInicio=" + horaInicio + ", horaFim=" + horaFim + ", getId()=" + getId()
                + ", getProfissional()=" + getProfissional() + ", getPosto()=" + getPosto() + ", getDiaSemana()="
                + getDiaSemana() + ", getHoraInicio()=" + getHoraInicio() + ", getHoraFim()=" + getHoraFim() + "]";
    }
    
}