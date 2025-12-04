package br.com.medcon.vo;

public class Especialidade {
    private int id;
    private String nome; 
    private String descricao; 
    
    public Especialidade() {}
    public Especialidade(int id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() {
        return "Especialidade: ID= " + getId() + " | nome= " + getNome() + " | Descricao= " + getDescricao();
    }
}