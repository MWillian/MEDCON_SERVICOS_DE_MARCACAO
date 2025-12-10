package br.com.medcon.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.vo.Agendamento;

public interface IAgendamentoDAO {
    void salvar(Agendamento ag) throws SQLException;
    void atualizar(Agendamento ag) throws SQLException;
    void deletar(int id) throws SQLException;
    Agendamento buscarPorId(int id) throws SQLException;
    List<Agendamento> buscarAgendamentosPorPaciente(int idPaciente) throws SQLException;
    List<Agendamento> listarTodos() throws SQLException;
}
