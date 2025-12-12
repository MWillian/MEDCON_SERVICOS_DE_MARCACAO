package br.com.medcon.bo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.AgendamentoDAO;
import br.com.medcon.vo.Agendamento;
import br.com.medcon.vo.Disponibilidade;

public class AgendamentoBO {
    private final AgendamentoDAO agendamentoDAO;

    public AgendamentoBO(AgendamentoDAO agendamentoDAO) {
        this.agendamentoDAO = agendamentoDAO;
    }
    
    public void salvar(Agendamento agendamento, int duracaoMinutos) throws NegocioException, SQLException {
        validarCamposObrigatorios(agendamento, duracaoMinutos);
        validarDataNoFuturo(agendamento.getDataHoraInicio());

        LocalDateTime dataHoraInicio = agendamento.getDataHoraInicio();
        LocalDateTime dataHoraFim = dataHoraInicio.plusMinutes(duracaoMinutos);
        agendamento.setDataHoraFim(dataHoraFim);

        validarConflitoHorario(agendamento.getProfissional().getId(), dataHoraInicio, dataHoraFim);

        agendamentoDAO.salvar(agendamento);
    }

    public List<LocalTime> calcularHorariosLivres(Disponibilidade disp, LocalDate dataAlvo, int duracaoMinutos)
            throws SQLException {
        List<LocalTime> horariosLivres = new ArrayList<>();

        if (dataAlvo.getDayOfWeek() != disp.getDiaSemana()) {
            return horariosLivres;
        }

        LocalTime cursor = disp.getHoraInicio();
        LocalTime fimExpediente = disp.getHoraFim();

        while (cursor.plusMinutes(duracaoMinutos).isBefore(fimExpediente)
                || cursor.plusMinutes(duracaoMinutos).equals(fimExpediente)) {

            LocalDateTime slotInicio = LocalDateTime.of(dataAlvo, cursor);
            LocalDateTime slotFim = slotInicio.plusMinutes(duracaoMinutos);

            boolean ocupado = agendamentoDAO.existeConflitoHorario(
                    disp.getProfissional().getId(),
                    slotInicio,
                    slotFim);

            if (!ocupado) {
                horariosLivres.add(cursor);
            }

            cursor = cursor.plusMinutes(duracaoMinutos);
        }

        return horariosLivres;
    }

    public List<Agendamento> listarTodos() throws SQLException {
        return agendamentoDAO.listarTodos();
    }

    public Agendamento buscarPorId(int id) throws NegocioException, SQLException {
        Agendamento agendamento = agendamentoDAO.buscarPorId(id);

        if (agendamento == null) {
            throw new NegocioException("Erro: Agendamento com ID " + id + " não encontrado.");
        }

        return agendamento;
    }

    public List<Agendamento> buscarAgendamentosPorPaciente(int idPaciente) throws SQLException {
        return agendamentoDAO.buscarAgendamentosPorPaciente(idPaciente);
    }

    private void validarCamposObrigatorios(Agendamento agendamento, int duracaoMinutos) throws NegocioException {
        if (agendamento.getPaciente() == null || agendamento.getPaciente().getId() <= 0) {
            throw new NegocioException("Erro: Paciente é obrigatório.");
        }

        if (agendamento.getProfissional() == null || agendamento.getProfissional().getId() <= 0) {
            throw new NegocioException("Erro: Profissional é obrigatório.");
        }

        if (agendamento.getPosto() == null || agendamento.getPosto().getId() <= 0) {
            throw new NegocioException("Erro: Posto de saúde é obrigatório.");
        }

        if (agendamento.getDataHoraInicio() == null) {
            throw new NegocioException("Erro: A data e hora do agendamento são obrigatórias.");
        }

        if (duracaoMinutos <= 0) {
            throw new NegocioException("Erro: Duração do serviço inválida.");
        }
    }

    private void validarDataNoFuturo(LocalDateTime dataHora) throws NegocioException {
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new NegocioException("Erro: Não é possível agendar para uma data/hora no passado.");
        }
    }

    private void validarConflitoHorario(int idProfissional, LocalDateTime inicio, LocalDateTime fim)
            throws NegocioException, SQLException {
        boolean temConflito = agendamentoDAO.existeConflitoHorario(idProfissional, inicio, fim);

        if (temConflito) {
            throw new NegocioException("Erro: Horário indisponível! O profissional já possui agendamento neste intervalo.");
        }
    }
}
