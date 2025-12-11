package br.com.medcon.bo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.AgendamentoDAO;
import br.com.medcon.vo.Agendamento;
import br.com.medcon.vo.Disponibilidade;
import br.com.medcon.vo.Paciente;
import br.com.medcon.vo.ProfissionalSaude;
import br.com.medcon.vo.PostoSaude;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AgendamentoBOTest {

    @Mock
    private AgendamentoDAO agendamentoDAO;

    private AgendamentoBO agendamentoBO;

    @Before
    public void setUp() {
        agendamentoBO = new AgendamentoBO(agendamentoDAO);
    }

    // Teste de salvar

    @Test
    public void salvar_deveCalcularDataHoraFimESalvarQuandoNaoHaConflito() throws Exception {

        Agendamento agendamento = mock(Agendamento.class);
        Paciente paciente = mock(Paciente.class);
        ProfissionalSaude profissional = mock(ProfissionalSaude.class);
        PostoSaude posto = mock(PostoSaude.class);

        LocalDateTime inicio = LocalDateTime.of(2025, 1, 1, 10, 0);
        int duracaoMinutos = 30;

        when(agendamento.getPaciente()).thenReturn(paciente);
        when(agendamento.getProfissional()).thenReturn(profissional);
        when(agendamento.getPosto()).thenReturn(posto);
        when(agendamento.getDataHoraInicio()).thenReturn(inicio);
        when(profissional.getId()).thenReturn(1);


        when(agendamentoDAO.existeConflitoHorario(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);

        agendamentoBO.salvar(agendamento, duracaoMinutos);

        LocalDateTime fimEsperado = inicio.plusMinutes(duracaoMinutos);
        verify(agendamento).setDataHoraFim(fimEsperado);
        verify(agendamentoDAO, times(1)).salvar(agendamento);
    }

    @Test(expected = NegocioException.class)
    public void salvar_deveLancarExcecaoQuandoDadosIncompletos() throws Exception {
        Agendamento agendamento = mock(Agendamento.class);
        when(agendamento.getPaciente()).thenReturn(null);
        when(agendamento.getProfissional()).thenReturn(null);
        when(agendamento.getPosto()).thenReturn(null);

        agendamentoBO.salvar(agendamento, 30);
    }

    @Test(expected = NegocioException.class)
    public void salvar_deveLancarExcecaoQuandoDataHoraInicioNula() throws Exception {
        Agendamento agendamento = mock(Agendamento.class);
        Paciente paciente = mock(Paciente.class);
        ProfissionalSaude profissional = mock(ProfissionalSaude.class);
        PostoSaude posto = mock(PostoSaude.class);

        when(agendamento.getPaciente()).thenReturn(paciente);
        when(agendamento.getProfissional()).thenReturn(profissional);
        when(agendamento.getPosto()).thenReturn(posto);
        when(agendamento.getDataHoraInicio()).thenReturn(null);

        agendamentoBO.salvar(agendamento, 30);
    }

    @Test(expected = NegocioException.class)
    public void salvar_deveLancarExcecaoQuandoDuracaoInvalida() throws Exception {
        Agendamento agendamento = mock(Agendamento.class);
        Paciente paciente = mock(Paciente.class);
        ProfissionalSaude profissional = mock(ProfissionalSaude.class);
        PostoSaude posto = mock(PostoSaude.class);

        when(agendamento.getPaciente()).thenReturn(paciente);
        when(agendamento.getProfissional()).thenReturn(profissional);
        when(agendamento.getPosto()).thenReturn(posto);
        when(agendamento.getDataHoraInicio()).thenReturn(LocalDateTime.now());

        int duracaoInvalida = 0; // <= 0 deve gerar exceção

        agendamentoBO.salvar(agendamento, duracaoInvalida);
    }

    @Test(expected = NegocioException.class)
    public void salvar_deveLancarExcecaoQuandoHaConflitoHorario() throws Exception {
        Agendamento agendamento = mock(Agendamento.class);
        Paciente paciente = mock(Paciente.class);
        ProfissionalSaude profissional = mock(ProfissionalSaude.class);
        PostoSaude posto = mock(PostoSaude.class);

        LocalDateTime inicio = LocalDateTime.of(2025, 1, 1, 10, 0);
        int duracaoMinutos = 30;

        when(agendamento.getPaciente()).thenReturn(paciente);
        when(agendamento.getProfissional()).thenReturn(profissional);
        when(agendamento.getPosto()).thenReturn(posto);
        when(agendamento.getDataHoraInicio()).thenReturn(inicio);
        when(profissional.getId()).thenReturn(1);

        when(agendamentoDAO.existeConflitoHorario(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        agendamentoBO.salvar(agendamento, duracaoMinutos);
    }
}
