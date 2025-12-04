package br.com.medcon.vo;


public class TipoServico {
    private int id;
    private String nome; 
    private int duracaoMinutos;
    private Especialidade especialidadeNecessaria; 

    public TipoServico() {}
    public TipoServico(int id, String nome, int duracaoMinutos, Especialidade especialidadeNecessaria) {
        this.id = id;
        this.nome = nome;
        this.duracaoMinutos = duracaoMinutos;
        this.especialidadeNecessaria = especialidadeNecessaria;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public int getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(int duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }

    public Especialidade getEspecialidadeNecessaria() { return especialidadeNecessaria; }
    public void setEspecialidadeNecessaria(Especialidade especialidadeNecessaria) { this.especialidadeNecessaria = especialidadeNecessaria; }

    @Override
    public String toString() {
        return "Tipo Serviço: ID= " + getId() + " | nome= " + getNome() 
        + " | Duração em minutos= " + getDuracaoMinutos() + " | Especialidade Necessaria= " + getEspecialidadeNecessaria();
    }
}