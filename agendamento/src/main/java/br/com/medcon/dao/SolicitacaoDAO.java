package br.com.medcon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import br.com.medcon.enums.Prioridade;
import br.com.medcon.enums.StatusSolicitacao;
import br.com.medcon.interfaces.ISolicitacaoDAO;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.Paciente;
import br.com.medcon.vo.Solicitacao;
import br.com.medcon.vo.TipoServico;

public class SolicitacaoDAO implements ISolicitacaoDAO {

    private final ConexaoFactory factory;

    public SolicitacaoDAO() {
        this.factory = new ConexaoFactory();
    }

    @Override
    public void salvar(Solicitacao solicitacao) throws SQLException {
        String sqlSolicitacao = "INSERT INTO tb_solicitacao (id_paciente, id_tipo_servico, status, prioridade) VALUES (?, ?, ?, ?);";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sqlSolicitacao)) {
            stmt.setInt(1, solicitacao.getPaciente().getId());
            stmt.setInt(2, solicitacao.getTipoServico().getId());
            stmt.setString(3, solicitacao.getStatus().name());
            stmt.setInt(4, solicitacao.getPrioridade().ordinal());
            stmt.execute();
        }
    }

    @Override
    public void atualizar(Solicitacao solicitacao) throws SQLException {
        String sql = "UPDATE tb_solicitacao SET id_paciente= ?, id_tipo_servico=?, status=?, prioridade=? WHERE id=?;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, solicitacao.getPaciente().getId());
            stmt.setInt(2, solicitacao.getTipoServico().getId());
            stmt.setString(3, solicitacao.getStatus().name());
            stmt.setInt(4, solicitacao.getPrioridade().ordinal());
            stmt.setInt(5, solicitacao.getId());
            stmt.execute();
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM tb_solicitacao WHERE id = ?;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.execute();
        }
    }

    @Override
    public Solicitacao buscarPorId(int id) throws SQLException {
        Solicitacao solicitacao = null;
        String sql = "SELECT tb_solicitacao.*, tb_paciente.*, tb_tipo_servico.*, tb_pessoa.*, tb_especialidade.* "
                + "FROM tb_solicitacao "

                + "INNER JOIN tb_paciente "
                + "ON tb_solicitacao.id_paciente = tb_paciente.id_pessoa "

                + "INNER JOIN tb_pessoa "
                + "ON tb_paciente.id_pessoa = tb_pessoa.id "

                + "INNER JOIN tb_tipo_servico "
                + "ON tb_solicitacao.id_tipo_servico = tb_tipo_servico.id "

                + "INNER JOIN tb_especialidade "
                + "ON tb_tipo_servico.id_especialidade_necessaria = tb_especialidade.id "

                + "WHERE tb_solicitacao.id= ?;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    solicitacao = montarSolicitacao(result);
                }
            }
        }
        return solicitacao;
    }

    @Override
    public List<Solicitacao> buscarTodos() throws SQLException {
        List<Solicitacao> solicitacoes = new ArrayList<>();
        String sql = "SELECT tb_solicitacao.*, tb_paciente.*, tb_tipo_servico.*, tb_pessoa.*, tb_especialidade.* "
                + "FROM tb_solicitacao "

                + "INNER JOIN tb_paciente "
                + "ON tb_solicitacao.id_paciente = tb_paciente.id_pessoa "

                + "INNER JOIN tb_pessoa "
                + "ON tb_paciente.id_pessoa = tb_pessoa.id "

                + "INNER JOIN tb_tipo_servico "
                + "ON tb_solicitacao.id_tipo_servico = tb_tipo_servico.id "

                + "INNER JOIN tb_especialidade "
                + "ON tb_tipo_servico.id_especialidade_necessaria  = tb_especialidade.id;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet result = stmt.executeQuery()) {

            while (result.next()) {
                solicitacoes.add(montarSolicitacao(result));
            }
        }
        return solicitacoes;
    }

    @Override
    public List<Solicitacao> listarPendentesPorPrioridade(int codigo) throws SQLException {
        List<Solicitacao> solicitacoes = new ArrayList<>();
        String sql = "SELECT tb_solicitacao.*, tb_paciente.*, tb_tipo_servico.*, tb_pessoa.*, tb_especialidade.* "
                + "FROM tb_solicitacao "

                + "INNER JOIN tb_paciente "
                + "ON tb_solicitacao.id_paciente = tb_paciente.id_pessoa "

                + "INNER JOIN tb_pessoa "
                + "ON tb_paciente.id_pessoa = tb_pessoa.id "

                + "INNER JOIN tb_tipo_servico "
                + "ON tb_solicitacao.id_tipo_servico = tb_tipo_servico.id "

                + "INNER JOIN tb_especialidade "
                + "ON tb_tipo_servico.id_especialidade_necessaria  = tb_especialidade.id "

                + "WHERE tb_solicitacao.status='PENDENTE' AND tb_solicitacao.prioridade= ?;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigo);

            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    solicitacoes.add(montarSolicitacao(result));
                }
            }
        }
        return solicitacoes;
    }

    private Solicitacao montarSolicitacao(ResultSet rs) throws SQLException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Solicitacao solicitacao = new Solicitacao();
        Especialidade especialidade = montarEspecialidade(rs);
        TipoServico tipoServico = montarTipoServico(rs, especialidade);

        solicitacao.setId(rs.getInt("id"));
        solicitacao.setPaciente(montarPaciente(rs));
        solicitacao.setTipoServico(tipoServico);
        solicitacao.setDataSolicitacao(LocalDateTime.parse(rs.getString("data_solicitacao"), formato));
        solicitacao.setPrioridade(Prioridade.fromCodigo(rs.getInt("prioridade")));
        solicitacao.setStatus(StatusSolicitacao.valueOf(rs.getString("status")));
        return solicitacao;
    }

    private Paciente montarPaciente(ResultSet rs) throws SQLException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Paciente paciente = new Paciente();

        paciente.setId(rs.getInt("id_pessoa"));
        paciente.setNome(rs.getString("nome"));
        paciente.setCpf(rs.getString("cpf"));
        paciente.setCartaoSus(rs.getString("cartao_sus"));
        paciente.setDataNascimento(LocalDate.parse(rs.getString("data_nascimento"), formato));
        paciente.setTelefone(rs.getString("telefone"));
        paciente.setEndereco(rs.getString("endereco"));
        
        return paciente;
    }

    private TipoServico montarTipoServico(ResultSet result, Especialidade especialidade) throws SQLException {
        TipoServico tipoServico = new TipoServico(
                result.getInt("id"),
                result.getString("nome"),
                result.getInt("duracao_media_minutos"),
                especialidade);
        return tipoServico;
    }

    private Especialidade montarEspecialidade(ResultSet result) throws SQLException {
        Especialidade especialidade = new Especialidade(
                result.getInt("id"),
                result.getString("nome"),
                result.getString("descricao"));
        return especialidade;
    }
}