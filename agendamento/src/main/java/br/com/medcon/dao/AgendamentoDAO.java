package br.com.medcon.dao;

import br.com.medcon.enums.StatusAgendamento;
import br.com.medcon.interfaces.IDAO;
import br.com.medcon.vo.Agendamento;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.Paciente;
import br.com.medcon.vo.PostoSaude;
import br.com.medcon.vo.ProfissionalSaude;
import br.com.medcon.vo.Solicitacao;

import java.sql.*;
import java.time.LocalDateTime;
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
                (id_solicitacao, id_profissional, id_posto, data_hora_inicio, data_hora_fim, status, laudo)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, objeto.getSolicitacao().getId());
            stmt.setInt(2, objeto.getProfissional().getId());
            stmt.setInt(3, objeto.getPosto().getId());
            stmt.setTimestamp(4, Timestamp.valueOf(objeto.getDataHoraInicio()));
            stmt.setTimestamp(5, Timestamp.valueOf(objeto.getDataHoraFim()));
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
                SET id_solicitacao = ?, id_profissional = ?, id_posto = ?,
                    data_hora_inicio = ?, data_hora_fim = ?, status = ?, laudo = ?
                WHERE id = ?
                """;

        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, objeto.getSolicitacao().getId());
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
        String sql = """
                SELECT a.*, 
                       s.id AS s_id, s.paciente_id AS s_paciente_id, s.motivo AS s_motivo,
                       p.id AS prof_id, p.nome AS prof_nome, p.cargo AS prof_cargo,
                       ps.id AS posto_id, ps.nome AS posto_nome, ps.endereco AS posto_endereco, ps.telefone AS posto_telefone
                FROM tb_agendamento a
                JOIN tb_solicitacao s ON s.id = a.id_solicitacao
                JOIN tb_profissional p ON p.id = a.id_profissional
                JOIN tb_posto ps ON ps.id = a.id_posto
                WHERE a.id = ?
                """;

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

        String sql = """
                SELECT a.*, 
                       s.id AS s_id, s.paciente_id AS s_paciente_id, s.motivo AS s_motivo,
                       p.id AS prof_id, p.nome AS prof_nome, p.cargo AS prof_cargo,
                       ps.id AS posto_id, ps.nome AS posto_nome, ps.endereco AS posto_endereco, ps.telefone AS posto_telefone
                FROM tb_agendamento a
                JOIN tb_solicitacao s ON s.id = a.id_solicitacao
                JOIN tb_profissional p ON p.id = a.id_profissional
                JOIN tb_posto ps ON ps.id = a.id_posto
                ORDER BY a.data_hora_inicio
                """;

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
    public boolean existeConflitoHorario(int idProfissional, LocalDateTime inicio, LocalDateTime fim) throws SQLException {

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
            stmt.setTimestamp(2, Timestamp.valueOf(fim));   // fim do novo
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
    private Agendamento montarAgendamento(ResultSet r) throws SQLException {

        // --- Solicitacao ---
        Solicitacao sol = new Solicitacao();
        sol.setId(r.getInt("s_id"));
        sol.setPaciente(new Paciente(r.getString("s_paciente_id")));

        // --- Profissional ---
        ProfissionalSaude prof = new ProfissionalSaude();
        prof.setId(r.getInt("prof_id"));
        prof.setNome(r.getString("prof_nome"));
        Especialidade esp = new Especialidade();
        esp.setNome(r.getString("prof_cargo"));
        prof.setEspecialidade(esp);

        // --- Posto de Saúde ---
        PostoSaude posto = new PostoSaude();
        posto.setId(r.getInt("posto_id"));
        posto.setNome(r.getString("posto_nome"));
        posto.setEndereco(r.getString("posto_endereco"));
        posto.setTelefone(r.getString("posto_telefone"));

        // --- Agendamento ---
        Agendamento ag = new Agendamento();
        ag.setId(r.getInt("id"));
        ag.setSolicitacao(sol);
        ag.setProfissional(prof);
        ag.setPosto(posto);
        ag.setDataHoraInicio(r.getTimestamp("data_hora_inicio").toLocalDateTime());
        ag.setDataHoraFim(r.getTimestamp("data_hora_fim").toLocalDateTime());
        ag.setStatus(StatusAgendamento.valueOf(r.getString("status")));
        ag.setLaudo(r.getString("laudo"));

        return ag;
    }
}
