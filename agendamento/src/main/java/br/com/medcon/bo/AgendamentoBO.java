package br.com.medcon.bo;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.AgendamentoDAO;
import br.com.medcon.vo.Agendamento;

public class AgendamentoBO {
    private final AgendamentoDAO agendamentoDAO;

    public AgendamentoBO(AgendamentoDAO agendamentoDAO) {
        this.agendamentoDAO = agendamentoDAO;
    }

    public void salvar(Agendamento agendamento) {
        try {
            agendamentoDAO.salvar(agendamento);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public List<Agendamento> listarTodos() throws SQLException {
        return agendamentoDAO.listarTodos();
    }

    public Agendamento buscarPorId(int id) throws NegocioException {
        try {
            Agendamento agendamento = agendamentoDAO.buscarPorId(id);

            if (agendamento == null) {
                throw new NegocioException("Agendamento com ID " + id + " não encontrado.");
            }

            return agendamento;

        } catch (SQLException e) {
            throw new NegocioException("Erro ao buscar o Agendamento.");
        }
    }
}
