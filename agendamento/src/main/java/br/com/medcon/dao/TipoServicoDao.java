package br.com.medcon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.medcon.interfaces.ITipoServicoDAO;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.TipoServico;

public class TipoServicoDao implements ITipoServicoDAO {
    private final ConexaoFactory factory;

    public TipoServicoDao() {
        this.factory = new ConexaoFactory();
    }

    @Override
    public void salvar(TipoServico tipoServico, Especialidade id_especialidade) throws SQLException {
        String sql = "INSERT INTO tb_tipo_servico (nome, duracao_media_minutos, id_especialidade_necessaria) VALUES (?, ?, ?);";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipoServico.getNome());
            stmt.setInt(2, tipoServico.getDuracaoMinutos());
            stmt.setInt(3, id_especialidade.getId());
            stmt.execute();
        }
    }

    @Override
    public void atualizar(TipoServico tipoServico, Especialidade id_especialidade) throws SQLException {
        String sql = "UPDATE tb_tipo_servico SET nome = ?, duracao_media_minutos = ?, id_especialidade_necessaria = ? WHERE id = ?;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipoServico.getNome());
            stmt.setInt(2, tipoServico.getDuracaoMinutos());
            stmt.setInt(3, id_especialidade.getId());
            stmt.setLong(4, tipoServico.getId());
            stmt.execute();
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM tb_tipo_servico WHERE id = ?;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public TipoServico buscarPorId(int id) throws SQLException {
        String sql = 
        "SELECT " +
        " tb_tipo_servico.id, " +
        " tb_tipo_servico.nome, " +
        " tb_tipo_servico.duracao_media_minutos, " +
        " tb_tipo_servico.id_especialidade_necessaria, " +
        " tb_especialidade.id AS especialidade_id, " +
        " tb_especialidade.nome AS especialidade_nome, " +
        " tb_especialidade.descricao AS especialidade_descricao " +
        "FROM tb_tipo_servico " +
        "INNER JOIN tb_especialidade " +
        "ON tb_tipo_servico.id_especialidade_necessaria = tb_especialidade.id " +
        "WHERE tb_tipo_servico.id = ?;";

        TipoServico tipoServico = null;
        Especialidade especialidade = null;
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if(result.next()) {
                    especialidade = montarEspecialidade(result);
                    tipoServico = montarTipoServico(result, especialidade);
                }
            }
        }

        return tipoServico;
    }

    @Override
    public List<TipoServico> listarTodos() throws SQLException {
        List<TipoServico> lista = new ArrayList<>();
        String sql = 
        "SELECT " +
        " tb_tipo_servico.id, " +
        " tb_tipo_servico.nome, " +
        " tb_tipo_servico.duracao_media_minutos, " +
        " tb_tipo_servico.id_especialidade_necessaria, " +
        " tb_especialidade.id AS especialidade_id, " +
        " tb_especialidade.nome AS especialidade_nome, " +
        " tb_especialidade.descricao AS especialidade_descricao " +
        "FROM tb_tipo_servico " +
        "INNER JOIN tb_especialidade " +
        "ON tb_tipo_servico.id_especialidade_necessaria = tb_especialidade.id;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet result = stmt.executeQuery()) {
            while (result.next()) {
                lista.add(montarTipoServico(result, montarEspecialidade(result)));
            }
        }
        return lista;
    }

    private TipoServico montarTipoServico(ResultSet result, Especialidade especialidade) throws SQLException{
        TipoServico tipoServico = new TipoServico(
            result.getLong("id"),
            result.getString("nome"),
            result.getInt("duracao_media_minutos"),
            especialidade);
        
        return tipoServico;
    }

    private Especialidade montarEspecialidade(ResultSet result) throws SQLException {
        Especialidade especialidade = new Especialidade(
            result.getInt("especialidade_id"),
            result.getString("especialidade_nome"),
            result.getString("especialidade_descricao"));
        return especialidade;
    }
}
