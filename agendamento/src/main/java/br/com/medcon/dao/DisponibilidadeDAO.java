package br.com.medcon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    public DisponibilidadeDAO() {
        this.factory = new ConexaoFactory();
    }

    @Override
    public void salvar(Disponibilidade disponibilidade) throws SQLException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");
        String sql = "INSERT INTO tb_disponibilidade (id_profissional, id_posto, dia_semana, hora_inicio, hora_fim) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, disponibilidade.getProfissional().getId());
            stmt.setInt(2, disponibilidade.getPosto().getId());
            stmt.setString(3, disponibilidade.getDiaSemana().toString());
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
            stmt.setString(3, disponibilidade.getDiaSemana().toString());
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
    public Disponibilidade buscarPorId(int id) throws SQLException {
        Disponibilidade disponibilidade = null;
        String sql = "SELECT " +
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
                "ON d.id_posto = po.id " +

                "WHERE d.id = ?;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    disponibilidade = monstarDisponibilidade(result);
                }
            }
        }
        return disponibilidade;
    }

    @Override
    public List<Disponibilidade> buscarTodos() throws SQLException {
        List<Disponibilidade> lista = new ArrayList<>();
        String sql = "SELECT " +
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
                "ON d.id_posto = po.id;";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet result = stmt.executeQuery()) {
            while (result.next()) {
                lista.add(monstarDisponibilidade(result));
            }
        }
        return lista;
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
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");
        Disponibilidade disponibilidade = new Disponibilidade();

        disponibilidade.setId(rs.getInt("disp_id"));
        disponibilidade.setProfissional(montarProfissional(rs));
        disponibilidade.setPosto(montarPostoSaude(rs));
        disponibilidade.setDiaSemana(DayOfWeek.valueOf(rs.getString("disp_dia_semana")));
        disponibilidade.setHoraInicio(LocalTime.parse(rs.getString("disp_hora_inicio"), formato));
        disponibilidade.setHoraFim(LocalTime.parse(rs.getString("disp_hora_fim"), formato));

        return disponibilidade;
    }

    private ProfissionalSaude montarProfissional(ResultSet rs) throws SQLException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ProfissionalSaude ps = new ProfissionalSaude();

        ps.setId(rs.getInt("prof_id_pessoa"));
        ps.setNome(rs.getString("pes_nome"));
        ps.setCpf(rs.getString("pes_cpf"));
        ps.setDataNascimento(LocalDate.parse(rs.getString("pes_data_nascimento"), formato));
        ps.setTelefone(rs.getString("pes_telefone"));
        ps.setEndereco(rs.getString("pes_endereco"));
        ps.setRegistroProfissional(rs.getString("prof_registro_profissional"));
        ps.setTipo(CargoProfissional.valueOf(rs.getString("prof_tipo_profissional")));
        ps.setEspecialidade(montarEspecialidade(rs));

        return ps;
    }

    private Especialidade montarEspecialidade(ResultSet result) throws SQLException {
        Especialidade especialidade = new Especialidade();
        especialidade.setId(result.getInt("esp_id"));
        especialidade.setNome(result.getString(("esp_nome")));
        especialidade.setDescricao(result.getString("esp_descricao"));
        return especialidade;
    }

    private PostoSaude montarPostoSaude(ResultSet rs) throws SQLException {
        PostoSaude ps = new PostoSaude();
        ps.setId(rs.getInt("posto_id"));
        ps.setNome(rs.getString("posto_nome"));
        ps.setEndereco(rs.getString("posto_endereco"));
        ps.setTelefone(rs.getString("posto_telefone"));

        return ps;
    }
}
