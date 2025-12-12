package br.com.medcon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.com.medcon.enums.CargoProfissional;
import br.com.medcon.interfaces.IDisponibilidadeDAO;
import br.com.medcon.vo.Disponibilidade;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.PostoSaude;
import br.com.medcon.vo.ProfissionalSaude;

public class DisponibilidadeDAO implements IDisponibilidadeDAO {

    private final ConexaoFactory factory;

    private final String SQL_BASE = """
            SELECT 
                d.id AS disp_id, d.dia_semana, d.hora_inicio, d.hora_fim,
                
                prof.id_pessoa AS prof_id, prof.registro_profissional, prof.tipo_profissional,
                p_prof.nome AS prof_nome, p_prof.cpf AS prof_cpf, p_prof.telefone AS prof_tel, p_prof.data_nascimento AS prof_nasc,
                
                e.id AS esp_id, e.nome AS esp_nome, e.descricao AS esp_desc,
                
                posto.id AS posto_id, posto.nome AS posto_nome, posto.endereco AS posto_end, posto.telefone AS posto_tel
            
            FROM tb_disponibilidade d
            JOIN tb_profissional prof ON d.id_profissional = prof.id_pessoa
            JOIN tb_pessoa p_prof ON prof.id_pessoa = p_prof.id
            JOIN tb_especialidade e ON prof.id_especialidade = e.id
            JOIN tb_posto posto ON d.id_posto = posto.id
            """;

    private final String SQL_COMPLETO = "SELECT " +
            // tb_disponibilidade
            "d.id AS disp_id, " +
            "d.id_profissional AS disp_id_profissional, " +
            "d.id_posto AS disp_id_posto, " +
            "d.dia_semana AS disp_dia_semana, " +
            "d.hora_inicio AS disp_hora_inicio, " +
            "d.hora_fim AS disp_hora_fim, " +

            // tb_profissional
            "prof.id_pessoa AS prof_id_pessoa, " +
            "prof.id_especialidade AS prof_id_especialidade, " +
            "prof.registro_profissional AS prof_registro_profissional, " +
            "prof.tipo_profissional AS prof_tipo_profissional, " +

            // tb_pessoa
            "pes.id AS pes_id, " +
            "pes.nome AS pes_nome, " +
            "pes.cpf AS pes_cpf, " +
            "pes.data_nascimento AS pes_data_nascimento, " +
            "pes.endereco AS pes_endereco, " +
            "pes.telefone AS pes_telefone, " +

            // tb_especialidade
            "esp.id AS esp_id, " +
            "esp.nome AS esp_nome, " +
            "esp.descricao AS esp_descricao, " +

            // tb_posto
            "po.id AS posto_id, " +
            "po.nome AS posto_nome, " +
            "po.endereco AS posto_endereco, " +
            "po.telefone AS posto_telefone " +

            "FROM tb_disponibilidade d " +

            "INNER JOIN tb_profissional prof " +
            "ON d.id_profissional = prof.id_pessoa " +

            "INNER JOIN tb_pessoa pes " +
            "ON prof.id_pessoa = pes.id " +

            "INNER JOIN tb_especialidade esp " +
            "ON prof.id_especialidade = esp.id " +

            "INNER JOIN tb_posto po " +
            "ON d.id_posto = po.id";

    public DisponibilidadeDAO() {
        this.factory = new ConexaoFactory();
    }

    @Override
    public void salvar(Disponibilidade d) throws SQLException {
        String sql = "INSERT INTO tb_disponibilidade (id_profissional, id_posto, dia_semana, hora_inicio, hora_fim) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, d.getProfissional().getId());
            stmt.setInt(2, d.getPosto().getId());
            stmt.setString(3, d.getDiaSemana().name());
            stmt.setString(4, d.getHoraInicio().toString());
            stmt.setString(5, d.getHoraFim().toString());

            stmt.execute();
        }
    }

    @Override
    public void atualizar(Disponibilidade d) throws SQLException {
        String sql = "UPDATE tb_disponibilidade SET id_profissional=?, id_posto=?, dia_semana=?, hora_inicio=?, hora_fim=? WHERE id=?";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, d.getProfissional().getId());
            stmt.setInt(2, d.getPosto().getId());
            stmt.setString(3, d.getDiaSemana().name());
            stmt.setString(4, d.getHoraInicio().toString());
            stmt.setString(5, d.getHoraFim().toString());
            stmt.setInt(6, d.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM tb_disponibilidade WHERE id = ?;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.execute();
        }
    }

    @Override
    public Disponibilidade buscarPorId(int id) throws SQLException {
        String sql = SQL_BASE + " WHERE d.id = ?";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return montarDisponibilidade(rs);
            }
        }
        return null;
    }

    @Override
    public List<Disponibilidade> buscarPorEspecialidade(int idEspecialidade) throws SQLException {
        String sql = SQL_BASE + " WHERE e.id = ?";
        List<Disponibilidade> lista = new ArrayList<>();

        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEspecialidade);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    lista.add(montarDisponibilidade(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Disponibilidade> buscarPorMedico(int idMedico) throws SQLException {
        String sql = SQL_BASE + " WHERE prof.id_pessoa = ?";
        List<Disponibilidade> lista = new ArrayList<>();

        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMedico);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    lista.add(montarDisponibilidade(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Disponibilidade> buscarPorPosto(int idPosto) throws SQLException {
        String sql = SQL_BASE + " WHERE posto.id = ?";
        List<Disponibilidade> lista = new ArrayList<>();

        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPosto);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    lista.add(montarDisponibilidade(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Disponibilidade> buscarTodos() throws SQLException {
        List<Disponibilidade> lista = new ArrayList<>();
        
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(SQL_COMPLETO);
                ResultSet result = stmt.executeQuery()) {
            
            while (result.next()) {
                lista.add(montarDisponibilidadeCompleta(result));
            }
        }
        return lista;
    }

    private Disponibilidade montarDisponibilidade(ResultSet rs) throws SQLException {
        Disponibilidade d = new Disponibilidade();
        d.setId(rs.getInt("disp_id"));

        d.setDiaSemana(DayOfWeek.valueOf(rs.getString("dia_semana")));
        d.setHoraInicio(LocalTime.parse(rs.getString("hora_inicio")));
        d.setHoraFim(LocalTime.parse(rs.getString("hora_fim")));

        d.setProfissional(montarProfissional(rs));
        d.setPosto(montarPosto(rs));

        return d;
    }

    private Disponibilidade montarDisponibilidadeCompleta(ResultSet rs) throws SQLException {
        Disponibilidade d = new Disponibilidade();
        d.setId(rs.getInt("disp_id"));

        d.setDiaSemana(DayOfWeek.valueOf(rs.getString("disp_dia_semana")));
        d.setHoraInicio(LocalTime.parse(rs.getString("disp_hora_inicio")));
        d.setHoraFim(LocalTime.parse(rs.getString("disp_hora_fim")));

        d.setProfissional(montarProfissionalCompleto(rs));
        d.setPosto(montarPostoCompleto(rs));

        return d;
    }

    private ProfissionalSaude montarProfissional(ResultSet rs) throws SQLException {
        ProfissionalSaude p = new ProfissionalSaude();
        p.setId(rs.getInt("prof_id"));
        p.setNome(rs.getString("prof_nome"));
        p.setCpf(rs.getString("prof_cpf"));
        p.setTelefone(rs.getString("prof_tel"));

        String dataNasc = rs.getString("prof_nasc");
        if (dataNasc != null) {
            try {
                p.setDataNascimento(LocalDate.parse(dataNasc));
            } catch (Exception e) {
            }
        }

        p.setRegistroProfissional(rs.getString("registro_profissional"));
        p.setTipo(CargoProfissional.valueOf(rs.getString("tipo_profissional")));

        Especialidade e = new Especialidade();
        e.setId(rs.getInt("esp_id"));
        e.setNome(rs.getString("esp_nome"));
        e.setDescricao(rs.getString("esp_desc"));
        p.setEspecialidade(e);

        return p;
    }

    private ProfissionalSaude montarProfissionalCompleto(ResultSet rs) throws SQLException {
        ProfissionalSaude p = new ProfissionalSaude();
        p.setId(rs.getInt("prof_id_pessoa"));
        p.setNome(rs.getString("pes_nome"));
        p.setCpf(rs.getString("pes_cpf"));
        p.setTelefone(rs.getString("pes_telefone"));
        p.setEndereco(rs.getString("pes_endereco"));

        String dataNasc = rs.getString("pes_data_nascimento");
        if (dataNasc != null) {
            try {
                p.setDataNascimento(LocalDate.parse(dataNasc));
            } catch (Exception e) {
            }
        }

        p.setRegistroProfissional(rs.getString("prof_registro_profissional"));
        p.setTipo(CargoProfissional.valueOf(rs.getString("prof_tipo_profissional")));

        Especialidade e = new Especialidade();
        e.setId(rs.getInt("esp_id"));
        e.setNome(rs.getString("esp_nome"));
        e.setDescricao(rs.getString("esp_descricao"));
        p.setEspecialidade(e);

        return p;
    }

    private PostoSaude montarPosto(ResultSet rs) throws SQLException {
        PostoSaude ps = new PostoSaude();
        ps.setId(rs.getInt("posto_id"));
        ps.setNome(rs.getString("posto_nome"));
        ps.setEndereco(rs.getString("posto_end"));
        ps.setTelefone(rs.getString("posto_tel"));
        return ps;
    }

    private PostoSaude montarPostoCompleto(ResultSet rs) throws SQLException {
        PostoSaude ps = new PostoSaude();
        ps.setId(rs.getInt("posto_id"));
        ps.setNome(rs.getString("posto_nome"));
        ps.setEndereco(rs.getString("posto_endereco"));
        ps.setTelefone(rs.getString("posto_telefone"));
        return ps;
    }
}
