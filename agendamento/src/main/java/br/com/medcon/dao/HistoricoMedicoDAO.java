package br.com.medcon.dao;
import br.com.medcon.interfaces.IDAO;
import br.com.medcon.vo.HistoricoMedico;
import br.com.medcon.vo.Paciente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistoricoMedicoDAO implements IDAO<HistoricoMedico> {
    private final ConexaoFactory factory;
    public HistoricoMedicoDAO() {
        this.factory = new ConexaoFactory();
    }

    @Override
    public void salvar(HistoricoMedico historico) throws SQLException {
        String sql = "INSERT INTO tb_historico_medico (id_paciente, data_evento, categoria, descricao, observacoes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, historico.getPaciente().getId());
            stmt.setString(2, historico.getDataRegistro().toString());
            stmt.setString(3, historico.getTipoEvento()); 
            stmt.setString(4, historico.getDetalhes());   
            stmt.setString(5, historico.getObservacoes()); 
            stmt.execute();
        }
    }

    @Override
    public void atualizar(HistoricoMedico historico) throws SQLException {
        String sql = "UPDATE tb_historico_medico SET data_evento=?, categoria=?, descricao=?, observacoes=? WHERE id=?";
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, historico.getDataRegistro().toString());
            stmt.setString(2, historico.getTipoEvento());
            stmt.setString(3, historico.getDetalhes());
            stmt.setString(4, historico.getObservacoes());
            stmt.setLong(5, historico.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(int id) throws SQLException { 
        String sql = "DELETE FROM tb_historico_medico WHERE id=?";
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.execute();
        }
    }

    @Override
    public HistoricoMedico buscarPorId(int id) throws SQLException {
        String sql = "SELECT h.*, p.nome as nome_paciente " +
                     "FROM tb_historico_medico h " +
                     "JOIN tb_pessoa p ON h.id_paciente = p.id " +
                     "WHERE h.id = ?";
        HistoricoMedico historico = null;
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    historico = montarObjeto(result);
                }
            }
        }
        return historico;
    }

    @Override
    public List<HistoricoMedico> listarTodos() throws SQLException {
        String sql = "SELECT h.*, p.nome as nome_paciente " +
                     "FROM tb_historico_medico h " +
                     "JOIN tb_pessoa p ON h.id_paciente = p.id";
        List<HistoricoMedico> lista = new ArrayList<>();
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet result = stmt.executeQuery()) {
            while (result.next()) {
                lista.add(montarObjeto(result));
            }
        }
        return lista;
    }

    public List<HistoricoMedico> listarPorPaciente(int idPaciente) throws SQLException {
        String sql = "SELECT h.*, p.nome as nome_paciente " +
                     "FROM tb_historico_medico h " +
                     "JOIN tb_pessoa p ON h.id_paciente = p.id " +
                     "WHERE h.id_paciente = ? " +
                     "ORDER BY h.data_evento DESC"; 
        List<HistoricoMedico> lista = new ArrayList<>();
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPaciente);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    lista.add(montarObjeto(result));
                }
            }
        }
        return lista;
    }

    private HistoricoMedico montarObjeto(ResultSet result) throws SQLException {
        HistoricoMedico h = new HistoricoMedico();
        h.setId(result.getInt("id"));
        String dataTexto = result.getString("data_evento");
        if (dataTexto != null) {
            h.setDataRegistro(LocalDate.parse(dataTexto));
        }
        h.setTipoEvento(result.getString("categoria"));
        h.setDetalhes(result.getString("descricao"));
        Paciente p = new Paciente();
        p.setId(result.getInt("id_paciente"));
        p.setNome(result.getString("nome_paciente"));
        h.setPaciente(p);
        return h;
    }
}