package br.com.medcon.dao;

import java.security.DrbgParameters.Reseed;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.zip.DataFormatException;

import br.com.medcon.interfaces.IDisponibilidadeDAO;
import br.com.medcon.vo.Disponibilidade;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.PostoSaude;
import br.com.medcon.vo.ProfissionalSaude;

public class DisponibilidadeDAO implements IDisponibilidadeDAO {

    private final ConexaoFactory factory;

    public DisponibilidadeDAO() {
        this.factory = new ConexaoFactory();
    }

    @Override
    public void salvar(Disponibilidade disponibilidade) throws SQLException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");
        String sql = "INSERTO INTO tb_disponibilidade (id_profissional, id_posto, dia_semana, hora_inicio, hora_fim) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, disponibilidade.getProfissional().getId());
            stmt.setInt(2, disponibilidade.getPosto().getId());
            stmt.setString(3, disponibilidade.getDiaSemana().getDisplayName(TextStyle.FULL, Locale.of("pt", "BR")));
            stmt.setString(4, disponibilidade.getHoraInicio().format(formato));
            stmt.setString(5, disponibilidade.getHoraFim().format(formato));
            stmt.execute();
        }
    }

    @Override
    public void atualizar(Disponibilidade disponibilidade) throws SQLException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");
        String sql = "UPDATE tb_disponibilidade SET id_profissional= ?, id_posto= ?, dia_semana= ?, hora_inicio= ?, hora_fim= ? WHERE id=?;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, disponibilidade.getProfissional().getId());
            stmt.setInt(2, disponibilidade.getPosto().getId());
            stmt.setString(3, disponibilidade.getDiaSemana().getDisplayName(TextStyle.FULL, Locale.of("pt", "BR")));
            stmt.setString(4, disponibilidade.getHoraInicio().format(formato));
            stmt.setString(5, disponibilidade.getHoraFim().format(formato));
            stmt.setInt(6, disponibilidade.getId());
            stmt.execute();
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM tb_disponibilidade WHERE id= ?;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.execute();
        }
    }

    @Override
    public Disponibilidade duscarPorId(int id) throws SQLException {
        Disponibilidade disponibilidade = null;
        String sql = "SELECT tb_disponibilidade.*, tb_profissional. *, tb_pessoa.*, tb_especialidade.*, tb_posto.* "
                + "FROM tb_disponibilidade "

                + "INNER JOIN tb_profissional "
                + "ON tb_disponibilidade.id = tb_profissional.id_pessoa "

                + "INNER JOIN tb_pessoa "
                + "ON tb_profissional.id_pessoa = tb_pessoa.id "

                + "INNER JOIN tb_especialidade "
                + "ON tb_profissional.id_especialidade = tb_especialidade.id"

                + "INNER JOIN tb_posto "
                + "ON tb_disponibilidade.id_posto = tb_posto.id "
                
                + "WHERE tb_disponibilidade.id = ?;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                disponibilidade = monstarDisponibilidade(result);
            }
        }
        return disponibilidade;
    }

    @Override
    public List<Disponibilidade> buscarTodos() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarTodos'");
    }

    @Override
    public Disponibilidade buscaPorMedico(int idMedico) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscaPorMedico'");
    }

    @Override
    public Disponibilidade buscaPorPosto(int idMedico) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscaPorPosto'");
    }

    private Disponibilidade monstarDisponibilidade(ResultSet rs) throws SQLException {
        Disponibilidade disponibilidade = new Disponibilidade();
        disponibilidade.setId(rs.getInt("id"));
        disponibilidade.setProfissional(null);
        return disponibilidade;
    }

    private ProfissionalSaude montarProfissional(ResultSet rs) throws SQLException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ProfissionalSaude ps = new ProfissionalSaude();

        ps.setId(rs.getInt("id_pessoa"));
        ps.setNome(rs.getString("nome"));
        ps.setCpf(rs.getString("cpf"));
        ps.setDataNascimento(LocalDate.parse(rs.getString("data_nascimento"), formato));
        ps.setTelefone(rs.getString( "telefone"));
        ps.setEndereco(rs.getString("endereco"));

        return ps;
    }

    private Especialidade montarObjeto(ResultSet result) throws SQLException{
        Especialidade especialidade = new Especialidade();
        especialidade.setId(result.getInt("id"));
        especialidade.setNome(result.getString(("nome")));
        especialidade.setDescricao(result.getString("descricao"));
        return especialidade;
    }

    private PostoSaude montarPostoSaude(ResultSet rs) throws SQLException {
        PostoSaude ps = new PostoSaude();

        return ps;
    }
}
