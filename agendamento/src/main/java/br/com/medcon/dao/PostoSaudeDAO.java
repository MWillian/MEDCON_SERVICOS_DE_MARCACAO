package br.com.medcon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.medcon.interfaces.IDAO;
import br.com.medcon.vo.PostoSaude;

public class PostoSaudeDAO implements IDAO<PostoSaude> {
    private final ConexaoFactory factory;

    public PostoSaudeDAO() {
        this.factory = new ConexaoFactory();
    }

    @Override
    public void atualizar(PostoSaude objeto) throws SQLException {
        String sql = "UPDATE tb_posto SET nome = ?, endereco = ?, telefone = ? WHERE id = ?";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objeto.getNome());
            stmt.setString(2, objeto.getEndereco());
            stmt.setString(3, objeto.getTelefone());
            stmt.setInt(4, objeto.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public PostoSaude buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM tb_posto WHERE id = ?";
        PostoSaude posto = null;
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    posto = montarObjeto(result);
                }
            }
        }
        return posto;
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM tb_posto WHERE id = ?";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.execute();
        }
    }

    @Override
    public List<PostoSaude> listarTodos() throws SQLException {
        String sql = "SELECT * FROM tb_posto";
        List<PostoSaude> postosCadastrados = new ArrayList<>();
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet result = stmt.executeQuery()) {
            while (result.next()) {
                postosCadastrados.add(montarObjeto(result));
            }
        }
        return postosCadastrados;
    }

    @Override
    public void salvar(PostoSaude posto) throws SQLException {
        String sql = "INSERT INTO tb_posto (nome, endereco, telefone) VALUES (?, ?, ?)";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, posto.getNome());
            stmt.setString(2, posto.getEndereco());
            stmt.setString(3, posto.getTelefone());
            stmt.execute();
        }
    }

    public PostoSaude buscarPorNomeEnderecoTelefoneIgnoreCase(String nome, String endereco, String telefone)
            throws SQLException {
        String sql = "SELECT * FROM tb_posto "
                + "WHERE LOWER(nome) = LOWER(?) "
                + "OR LOWER(endereco) = LOWER(?) "
                + "OR telefone = ?";

        PostoSaude posto = null;
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, endereco);
            stmt.setString(3, telefone);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    posto = montarObjeto(result);
                }
            }
        }
        return posto;
    }

    private PostoSaude montarObjeto(ResultSet result) throws SQLException {
        PostoSaude posto = new PostoSaude();
        posto.setId(result.getInt("id"));
        posto.setNome(result.getString("nome"));
        posto.setEndereco(result.getString("endereco"));
        posto.setTelefone(result.getString("telefone"));
        return posto;
    }
}
