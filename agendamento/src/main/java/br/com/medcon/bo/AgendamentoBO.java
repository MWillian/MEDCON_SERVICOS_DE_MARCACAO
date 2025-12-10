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
        if (agendamento.getPaciente() == null || agendamento.getProfissional() == null || agendamento.getPosto() == null) {
            throw new NegocioException("Dados incompletos. Paciente, Profissional e Posto são obrigatórios.");
        }
        if (agendamento.getDataHoraInicio() == null) {
            throw new NegocioException("A data e hora do agendamento são obrigatórias.");
        }
        if (duracaoMinutos <= 0) {
            throw new NegocioException("Duração do serviço inválida.");
        }

        LocalDateTime inicio = agendamento.getDataHoraInicio();
        LocalDateTime fimCalculado = inicio.plusMinutes(duracaoMinutos);
        agendamento.setDataHoraFim(fimCalculado);

        boolean temConflito = agendamentoDAO.existeConflitoHorario(
                agendamento.getProfissional().getId(),
                inicio, 
                fimCalculado
        );

        if (temConflito) {
            throw new NegocioException("Horário indisponível! O profissional já possui agendamento neste intervalo.");
        }
        agendamentoDAO.salvar(agendamento);
    }

    public List<LocalTime> calcularHorariosLivres(Disponibilidade disp, LocalDate dataAlvo, int duracaoMinutos) throws SQLException {
        List<LocalTime> horariosLivres = new ArrayList<>();

        if (dataAlvo.getDayOfWeek() != disp.getDiaSemana()) {
            return horariosLivres;
        }

        LocalTime cursor = disp.getHoraInicio();
        LocalTime fimExpediente = disp.getHoraFim();

        while (cursor.plusMinutes(duracaoMinutos).isBefore(fimExpediente) || cursor.plusMinutes(duracaoMinutos).equals(fimExpediente)) {
            
            LocalDateTime slotInicio = LocalDateTime.of(dataAlvo, cursor);
            LocalDateTime slotFim = slotInicio.plusMinutes(duracaoMinutos);

            boolean ocupado = agendamentoDAO.existeConflitoHorario(
                disp.getProfissional().getId(),
                slotInicio, 
                slotFim
            );

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
            throw new NegocioException("Agendamento com ID " + id + " não encontrado.");
        }

        return agendamento;
    }

    public List<Agendamento> buscarAgendamentosPorPaciente(int idPaciente) throws NegocioException, SQLException {
        List<Agendamento> agendamento = agendamentoDAO.buscarAgendamentosPorPaciente(idPaciente);

        if (agendamento.size() == 0) {
            throw new NegocioException("Nenhum Agendamento encontrado.");
        }

        return agendamento;
    }
}
