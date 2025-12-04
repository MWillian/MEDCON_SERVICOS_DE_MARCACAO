package br.com.medcon.vo;

public class PostoSaude {
    private int id;
    private String nome;
    private String endereco; 
    private String telefone;
    

    public PostoSaude() {}
    public PostoSaude(int id, String nome, String endereco, String telefone) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id;}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    
    public String getTelefone() {return telefone;}
    public void setTelefone(String telefone) { this.telefone = telefone; }
}