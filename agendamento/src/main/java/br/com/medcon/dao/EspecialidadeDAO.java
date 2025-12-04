package br.com.medcon.dao;
import br.com.medcon.interfaces.IDAO;
import br.com.medcon.vo.Especialidade;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadeDAO implements IDAO<Especialidade>{
    private final ConexaoFactory factory;
    public EspecialidadeDAO(){
        this.factory = new ConexaoFactory(); //cria uma conexão específica com o SQLite
    }
    @Override
    public void salvar(Especialidade especialidade) throws SQLException {
        String sql = "INSERT INTO tb_especialidade (nome, descricao) VALUES (?, ?)";
        try (Connection conn = factory.getConexao();
        PreparedStatement stmt = conn.prepareStatement(sql)){ // prepara o comando para ser executado na conexão atual do banco
                stmt.setString(1,especialidade.getNome()); //substitui os ? para os parâmetros passados.
                stmt.setString(2,especialidade.getDescricao());
                stmt.execute(); //roda a string no banco
            }
    }
    @Override
    public void atualizar(Especialidade especialidade) throws SQLException {
        String sql = "UPDATE tb_especialidade SET nome = ?, descricao = ? WHERE id = ?";
        try (Connection conn = factory.getConexao();
        PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1,especialidade.getNome());
                stmt.setString(2,especialidade.getDescricao());
                stmt.setInt(3,especialidade.getId());
                stmt.executeUpdate();
            }
    }
    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM tb_especialidade WHERE id = ?";
        try (Connection conn = factory.getConexao();
        PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,id);
                stmt.execute();
            }
    }
    @Override
    public Especialidade buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM tb_especialidade WHERE id = ?";
        Especialidade especialidade = null;
        try (Connection conn = factory.getConexao();
        PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, id);
                try(ResultSet result = stmt.executeQuery()){ //executa a string no banco nos casos de select. Sempre deve ser atribuída para uma variável ResultSet(recebe o resultado, tipo uma planilha)
                    if (result.next()){ // o resultset sempre mantém um cursor na primeira linha. quando chamamos result.next(), movemos o cursor uma linha para baixo. Se retornar true, o cursor desceu (significa que tem mais linhas para percorrer).
                        especialidade = montarObjeto(result);
                    }
                }
            }
        return especialidade;
    }
    @Override
    public List<Especialidade> listarTodos() throws SQLException {
        String sql = "SELECT * FROM tb_especialidade";
        List<Especialidade> lista = new ArrayList<>();
        try (Connection conn = factory.getConexao();
        PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet result = stmt.executeQuery()){
                while(result.next()){
                    lista.add(montarObjeto(result));
                }
            }
        return lista;
    }
    private Especialidade montarObjeto(ResultSet result) throws SQLException{
        Especialidade especialidade = new Especialidade();
        especialidade.setId(result.getInt("id"));
        especialidade.setNome(result.getString(("nome")));
        especialidade.setDescricao(result.getString("descricao"));
        return especialidade;
    }
}
