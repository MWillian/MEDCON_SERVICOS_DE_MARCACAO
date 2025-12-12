package br.com.medcon.bo;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.DisponibilidadeDAO;
import br.com.medcon.vo.Disponibilidade;
import br.com.medcon.vo.Especialidade;

public class DisponibilidadeBO {
    private final DisponibilidadeDAO disponibilidadeDAO;

    public DisponibilidadeBO(DisponibilidadeDAO disponibilidadeDAO) {
        this.disponibilidadeDAO = disponibilidadeDAO;
    }

    public void salvar(Disponibilidade disponibilidade) throws NegocioException, SQLException {
        validarCamposObrigatorios(disponibilidade);
        validarHorarios(disponibilidade);
        validarConflitoDisponibilidade(disponibilidade);
        disponibilidadeDAO.salvar(disponibilidade);
    }

    public List<Disponibilidade> buscarTodos() throws SQLException {
        return disponibilidadeDAO.buscarTodos();
    }

    public Disponibilidade buscarPorId(int id) throws NegocioException, SQLException {
        Disponibilidade disp = disponibilidadeDAO.buscarPorId(id);
        if (disp == null) {
            throw new NegocioException("Erro: Disponibilidade com ID " + id + " não encontrada.");
        }
        return disp;
    }

    public List<Disponibilidade> buscarPorMedico(int id) throws SQLException {
        return disponibilidadeDAO.buscarPorMedico(id);
    }

    public List<Disponibilidade> buscarPorEspecialidade(Especialidade especialidade)
            throws SQLException, NegocioException {
        if (especialidade == null || especialidade.getId() <= 0) {
            throw new NegocioException("Erro: Especialidade inválida para a busca.");
        }

        return this.disponibilidadeDAO.buscarPorEspecialidade(especialidade.getId());
    }

    private void validarCamposObrigatorios(Disponibilidade d) throws NegocioException {
        if (d.getProfissional() == null || d.getProfissional().getId() <= 0) {
            throw new NegocioException("Erro: A disponibilidade deve estar vinculada a um profissional.");
        }

        if (d.getPosto() == null || d.getPosto().getId() <= 0) {
            throw new NegocioException("Erro: A disponibilidade deve estar vinculada a um posto de saúde.");
        }

        if (d.getDiaSemana() == null) {
            throw new NegocioException("Erro: O dia da semana é obrigatório.");
        }
    }

    private void validarHorarios(Disponibilidade d) throws NegocioException {
        if (d.getHoraInicio() == null || d.getHoraFim() == null) {
            throw new NegocioException("Erro: Horários de início e fim são obrigatórios.");
        }

        if (!d.getHoraInicio().isBefore(d.getHoraFim())) {
            throw new NegocioException(
                    "Erro: Horário inválido. A hora de início (" + d.getHoraInicio()
                            + ") deve ser anterior à hora de fim (" + d.getHoraFim() + ").");
        }
    }

    private void validarConflitoDisponibilidade(Disponibilidade nova) throws NegocioException, SQLException {
        List<Disponibilidade> disponibilidadesExistentes = disponibilidadeDAO
                .buscarPorMedico(nova.getProfissional().getId());

        for (Disponibilidade existente : disponibilidadesExistentes) {
            if (existente.getDiaSemana() == nova.getDiaSemana()
                    && existente.getPosto().getId() == nova.getPosto().getId()) {

                boolean horarioConflita = !(nova.getHoraFim().isBefore(existente.getHoraInicio())
                        || nova.getHoraInicio().isAfter(existente.getHoraFim()));

                if (horarioConflita) {
                    throw new NegocioException(
                            "Erro: Conflito de horário! O profissional já possui disponibilidade neste dia/horário neste posto.");
                }
            }
        }
    }
}
