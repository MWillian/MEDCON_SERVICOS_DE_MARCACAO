package br.com.medcon.bo;
import java.sql.SQLException;
import java.util.List;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.DisponibilidadeDAO;
import br.com.medcon.vo.Disponibilidade;
import br.com.medcon.vo.ProfissionalSaude;

public class DisponibilidadeBO {
    private final DisponibilidadeDAO disponibilidadeDAO;
    
    public DisponibilidadeBO(DisponibilidadeDAO disponibilidadeDAO) {
        this.disponibilidadeDAO = disponibilidadeDAO;
    }

    public void salvar(Disponibilidade disponibilidade) throws NegocioException, SQLException {
        ValidarCamposObrigatorios(disponibilidade);
        ValidarHorarios(disponibilidade);
        disponibilidadeDAO.salvar(disponibilidade);
    }

    public List<Disponibilidade> listarTodos() throws SQLException {
        return disponibilidadeDAO.buscarTodos();
    }

    public Disponibilidade buscarPorId(int id) throws NegocioException, SQLException {
        try {
            Disponibilidade esp = disponibilidadeDAO.buscarPorId(id);

            if (esp == null) {
                throw new NegocioException("Disponibilidade com ID " + id + " não encontrada.");
            }
            return esp;

        } catch (SQLException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    public ProfissionalSaude buscarPorMedico(int id) throws SQLException {
        return disponibilidadeDAO.buscaPorMedico(id);
    }
}
