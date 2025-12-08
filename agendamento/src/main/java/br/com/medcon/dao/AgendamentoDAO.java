package br.com.medcon.dao;

import br.com.medcon.enums.CargoProfissional;
import br.com.medcon.enums.StatusAgendamento;
import br.com.medcon.interfaces.IDAO;
import br.com.medcon.vo.Agendamento;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.Paciente;
import br.com.medcon.vo.PostoSaude;
import br.com.medcon.vo.ProfissionalSaude;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoDAO implements IDAO<Agendamento> {

    private final ConexaoFactory factory;

    public AgendamentoDAO() {
        this.factory = new ConexaoFactory();
    }

    // ------------------------------------------------------------
    // SALVAR
    // ------------------------------------------------------------
    @Override
    public void salvar(Agendamento objeto) throws SQLException {
        String sql = """
                INSERT INTO tb_agendamento
                (id_paciente, id_profissional, id_posto, data_hora_inicio, status, laudo_resultado)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, objeto.getPaciente().getId());
            stmt.setInt(2, objeto.getProfissional().getId());
            stmt.setInt(3, objeto.getPosto().getId());
            stmt.setTimestamp(4, Timestamp.valueOf(objeto.getDataHoraInicio()));
            // stmt.setTimestamp(5, Timestamp.valueOf(objeto.getDataHoraFim()));
            stmt.setString(6, objeto.getStatus().name());
            stmt.setString(7, objeto.getLaudo());

            stmt.execute();
        }
    }

    // ------------------------------------------------------------
    // ATUALIZAR
    // ------------------------------------------------------------
    @Override
    public void atualizar(Agendamento objeto) throws SQLException {

        String sql = """
                UPDATE tb_agendamento
                SET id_paciente = ?, id_profissional = ?, id_posto = ?,
                    data_hora_inicio = ?, data_hora_fim = ?, status = ?, laudo = ?
                WHERE id = ?
                """;

        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, objeto.getPaciente().getId());
            stmt.setInt(2, objeto.getProfissional().getId());
            stmt.setInt(3, objeto.getPosto().getId());
            stmt.setTimestamp(4, Timestamp.valueOf(objeto.getDataHoraInicio()));
            stmt.setTimestamp(5, Timestamp.valueOf(objeto.getDataHoraFim()));
            stmt.setString(6, objeto.getStatus().name());
            stmt.setString(7, objeto.getLaudo());
            stmt.setInt(8, objeto.getId());

            stmt.executeUpdate();
        }
    }

    // ------------------------------------------------------------
    // DELETAR
    // ------------------------------------------------------------
    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM tb_agendamento WHERE id = ?";

        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.execute();
        }
    }

    // ------------------------------------------------------------
    // BUSCAR POR ID (JOIN COMPLETO)
    // ------------------------------------------------------------
    @Override
    public Agendamento buscarPorId(int id) throws SQLException {
        String sql = "SELECT " +
                // tb_agendamento
                "    ag.id AS ag_id, " +
                "    ag.id_paciente AS ag_id_paciente, " +
                "    ag.id_profissional AS ag_id_profissional, " +
                "    ag.id_posto AS ag_id_posto, " +
                "    ag.data_hora_inicio AS ag_data_hora_inicio, " +
                // "    ag.data_hora_fim AS ag_data_hora_fim, " +
                "    ag.status AS ag_status, " +
                "    ag.laudo_resultado AS ag_laudo, " +
                // tb_paciente
                "    pac.id_pessoa AS pac_id_pessoa, " +
                "    pac.cartao_sus AS pac_cartao_sus, " +
                // tb_pessoa + tb_paciente
                "    pes_pac.id AS pes_pac_id, " +
                "    pes_pac.nome AS pes_pac_nome, " +
                "    pes_pac.cpf AS pes_pac_cpf, " +
                "    pes_pac.data_nascimento AS pes_pac_data_nascimento, " +
                "    pes_pac.telefone AS pes_pac_telefone, " +
                "    pes_pac.endereco AS pes_pac_endereco, " +
                // tb_profissional
                "    prof.id_pessoa AS prof_id_pessoa, " +
                "    prof.registro_profissional AS prof_registro_profissional, " +
                "    prof.tipo_profissional AS prof_tipo_profissional, " +
                "    prof.id_especialidade AS prof_id_especialidade, " +
                // tb_pessoa + tb_profissional
                "    pes_prof.id AS pes_prof_id, " +
                "    pes_prof.nome AS pes_prof_nome, " +
                "    pes_prof.cpf AS pes_prof_cpf, " +
                "    pes_prof.data_nascimento AS pes_prof_data_nascimento, " +
                "    pes_prof.telefone AS pes_prof_telefone, " +
                "    pes_prof.endereco AS pes_prof_endereco, " +
                // tb_especialidade
                "    esp.id AS esp_id, " +
                "    esp.nome AS esp_nome, " +
                "    esp.descricao AS esp_descricao, " +
                // tb_posto
                "    posto.id AS posto_id, " +
                "    posto.nome AS posto_nome, " +
                "    posto.endereco AS posto_endereco, " +
                "    posto.telefone AS posto_telefone " +

                "FROM tb_agendamento ag " +

                "INNER JOIN tb_paciente pac " +
                "    ON ag.id_paciente = pac.id_pessoa " +

                "INNER JOIN tb_pessoa pes_pac " +
                "    ON pac.id_pessoa = pes_pac.id " +

                "INNER JOIN tb_profissional prof " +
                "    ON ag.id_profissional = prof.id_pessoa " +

                "INNER JOIN tb_pessoa pes_prof " +
                "    ON prof.id_pessoa = pes_prof.id " +

                "INNER JOIN tb_especialidade esp " +
                "    ON prof.id_especialidade = esp.id " +

                "INNER JOIN tb_posto posto " +
                "    ON ag.id_posto = posto.id " + 
                
                "WHERE ag.id= ?;";

        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return montarAgendamento(result);
                }
            }
        }

        return null;
    }

    // ------------------------------------------------------------
    // LISTAR TODOS (JOIN COMPLETO)
    // ------------------------------------------------------------
    @Override
    public List<Agendamento> listarTodos() throws SQLException {
        String sql = "SELECT " +
                "    ag.id AS ag_id, " +
                "    ag.id_paciente AS ag_id_paciente, " +
                "    ag.id_profissional AS ag_id_profissional, " +
                "    ag.id_posto AS ag_id_posto, " +
                "    ag.data_hora_inicio AS ag_data_hora_inicio, " +
                // "    ag.data_hora_fim AS ag_data_hora_fim, " +
                "    ag.status AS ag_status, " +
                "    ag.laudo_resultado AS ag_laudo, " +

                "    pac.id_pessoa AS pac_id_pessoa, " +
                "    pac.cartao_sus AS pac_cartao_sus, " +

                "    pes_pac.id AS pes_pac_id, " +
                "    pes_pac.nome AS pes_pac_nome, " +
                "    pes_pac.cpf AS pes_pac_cpf, " +
                "    pes_pac.data_nascimento AS pes_pac_data_nascimento, " +
                "    pes_pac.telefone AS pes_pac_telefone, " +
                "    pes_pac.endereco AS pes_pac_endereco, " +

                "    prof.id_pessoa AS prof_id_pessoa, " +
                "    prof.registro_profissional AS prof_registro_profissional, " +
                "    prof.tipo_profissional AS prof_tipo_profissional, " +
                "    prof.id_especialidade AS prof_id_especialidade, " +

                "    pes_prof.id AS pes_prof_id, " +
                "    pes_prof.nome AS pes_prof_nome, " +
                "    pes_prof.cpf AS pes_prof_cpf, " +
                "    pes_prof.data_nascimento AS pes_prof_data_nascimento, " +
                "    pes_prof.telefone AS pes_prof_telefone, " +
                "    pes_prof.endereco AS pes_prof_endereco, " +

                "    esp.id AS esp_id, " +
                "    esp.nome AS esp_nome, " +
                "    esp.descricao AS esp_descricao, " +

                "    posto.id AS posto_id, " +
                "    posto.nome AS posto_nome, " +
                "    posto.endereco AS posto_endereco, " +
                "    posto.telefone AS posto_telefone " +

                "FROM tb_agendamento ag " +

                "INNER JOIN tb_paciente pac " +
                "    ON ag.id_paciente = pac.id_pessoa " +

                "INNER JOIN tb_pessoa pes_pac " +
                "    ON pac.id_pessoa = pes_pac.id " +

                "INNER JOIN tb_profissional prof " +
                "    ON ag.id_profissional = prof.id_pessoa " +

                "INNER JOIN tb_pessoa pes_prof " +
                "    ON prof.id_pessoa = pes_prof.id " +

                "INNER JOIN tb_especialidade esp " +
                "    ON prof.id_especialidade = esp.id " +

                "INNER JOIN tb_posto posto " +
                "    ON ag.id_posto = posto.id;";

        List<Agendamento> lista = new ArrayList<>();

        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet result = stmt.executeQuery()) {

            while (result.next()) {
                lista.add(montarAgendamento(result));
            }
        }

        return lista;
    }

    // ------------------------------------------------------------
    // VALIDAR CONFLITO DE HORÁRIO
    // ------------------------------------------------------------
    public boolean existeConflitoHorario(int idProfissional, LocalDateTime inicio, LocalDateTime fim)
            throws SQLException {

        String sql = """
                SELECT COUNT(*) AS qtd
                FROM tb_agendamento
                WHERE id_profissional = ?
                  AND (
                        (data_hora_inicio <= ? AND data_hora_fim >= ?) OR
                        (data_hora_inicio < ? AND data_hora_fim >= ?)
                      )
                """;

        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProfissional);
            stmt.setTimestamp(2, Timestamp.valueOf(fim)); // fim do novo
            stmt.setTimestamp(3, Timestamp.valueOf(inicio)); // início do novo
            stmt.setTimestamp(4, Timestamp.valueOf(inicio));
            stmt.setTimestamp(5, Timestamp.valueOf(inicio));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("qtd") > 0;
            }
        }

        return false;
    }

    // ------------------------------------------------------------
    // MONTAR OBJETO COMPLETO
    // ------------------------------------------------------------
    private Agendamento montarAgendamento(ResultSet rs) throws SQLException {
        // --- Agendamento ---
        Agendamento ag = new Agendamento();
        ag.setId(rs.getInt("ag_id"));
        ag.setPaciente(montarPaciente(rs));
        ag.setProfissional(montarProfissional(rs));
        ag.setPosto(montarPostoSaude(rs));
        ag.setDataHoraInicio(rs.getTimestamp("ag_data_hora_inicio").toLocalDateTime());
        // ag.setDataHoraFim(rs.getTimestamp("ag_data_hora_fim").toLocalDateTime());
        ag.setStatus(StatusAgendamento.valueOf(rs.getString("ag_status")));
        ag.setLaudo(rs.getString("ag_laudo"));

        return ag;
    }

    // --- Paciente ---
    private Paciente montarPaciente(ResultSet rs) throws SQLException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Paciente pac = new Paciente();

        pac.setId(rs.getInt("pes_pac_id"));
        pac.setNome(rs.getString("pes_pac_nome"));
        pac.setCpf(rs.getString("pes_pac_cpf"));
        pac.setDataNascimento(LocalDate.parse(rs.getString("pes_pac_data_nascimento"), formato));
        pac.setTelefone(rs.getString("pes_pac_telefone"));
        pac.setEndereco(rs.getString("pes_pac_endereco"));
        pac.setCartaoSus(rs.getString("pac_cartao_sus"));

        return pac;
    }

    // --- Profissional ---
    private ProfissionalSaude montarProfissional(ResultSet rs) throws SQLException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ProfissionalSaude ps = new ProfissionalSaude();

        ps.setId(rs.getInt("pes_prof_id"));
        ps.setNome(rs.getString("pes_prof_nome"));
        ps.setCpf(rs.getString("pes_prof_cpf"));
        ps.setDataNascimento(LocalDate.parse(rs.getString("pes_prof_data_nascimento"), formato));
        ps.setTelefone(rs.getString("pes_prof_telefone"));
        ps.setEndereco(rs.getString("pes_prof_endereco"));
        ps.setRegistroProfissional(rs.getString("prof_registro_profissional"));
        ps.setTipo(CargoProfissional.valueOf(rs.getString("prof_tipo_profissional")));
        ps.setEspecialidade(montarEspecialidade(rs));

        return ps;
    }

    // --- Especialidade ---
    private Especialidade montarEspecialidade(ResultSet result) throws SQLException {
        Especialidade especialidade = new Especialidade();
        especialidade.setId(result.getInt("esp_id"));
        especialidade.setNome(result.getString(("esp_nome")));
        especialidade.setDescricao(result.getString("esp_descricao"));
        return especialidade;
    }

    // --- Posto de Saúde ---
    private PostoSaude montarPostoSaude(ResultSet rs) throws SQLException {
        PostoSaude ps = new PostoSaude();
        ps.setId(rs.getInt("posto_id"));
        ps.setNome(rs.getString("posto_nome"));
        ps.setEndereco(rs.getString("posto_endereco"));
        ps.setTelefone("posto_telefone");

        return ps;
    }
}
