package br.com.medcon.vo;

public class ProfissionalPosto {
    private int id; 
    private ProfissionalSaude profissional; 
    private PostoSaude posto; 
    public ProfissionalPosto() {}
    public ProfissionalPosto(int id, ProfissionalSaude profissional, PostoSaude posto) {
        this.id = id;
        this.profissional = profissional;
        this.posto = posto;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public ProfissionalSaude getProfissional() { return profissional; }
    public void setProfissional(ProfissionalSaude profissional) { this.profissional = profissional; }

    public PostoSaude getPosto() { return posto; }
    public void setPosto(PostoSaude posto) { this.posto = posto; }
}