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
            throw new SQLException("Erro ao buscar a Disponibilidade.");
        }
    }

    //MÉTODOS AUXILIARES
    private void ValidarCamposObrigatorios(Disponibilidade d) throws NegocioException{
        if (d.getProfissional().getId() <= 0) {
            throw new NegocioException("A disponibilidade deve estar vinculada a um Profissional.");
        }

        if (d.getPosto() == null || d.getPosto().getId() <= 0) {
            throw new NegocioException("A disponibilidade deve estar vinculada a um Posto de Saúde.");
        }
        
        if (d.getDiaSemana() == null) {
            throw new NegocioException("O dia da semana é obrigatório.");
        }
    }

    private void ValidarHorarios(Disponibilidade d) throws NegocioException {
        if (d.getHoraInicio() == null || d.getHoraFim() == null) {
            throw new NegocioException("Horários de início e fim são obrigatórios.");
        }
        
        if (!d.getHoraInicio().isBefore(d.getHoraFim())) {
            throw new NegocioException("Horário inválido: A hora de início (" + d.getHoraInicio() + 
                                     ") deve ser anterior à hora de fim (" + d.getHoraFim() + ").");
        }
    }

    public ProfissionalSaude buscarPorMedico(int id) throws SQLException {
        return disponibilidadeDAO.buscaPorMedico(id);
    }
}
