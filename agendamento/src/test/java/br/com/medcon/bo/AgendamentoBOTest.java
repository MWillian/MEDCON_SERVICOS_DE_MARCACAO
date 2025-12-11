package br.com.medcon.bo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
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

    // Testes de salvar

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

    // Testes  de calcularHorariosLivres

    @Test
    public void calcularHorariosLivres_deveRetornarListaVaziaQuandoDiaSemanaNaoConfere() throws Exception {
        Disponibilidade disp = mock(Disponibilidade.class);
        LocalDate dataAlvo = LocalDate.of(2025, 1, 1);

        when(disp.getDiaSemana()).thenReturn(dataAlvo.plusDays(1).getDayOfWeek());

        List<LocalTime> horarios = agendamentoBO.calcularHorariosLivres(disp, dataAlvo, 30);

        assertNotNull(horarios);
        assertTrue(horarios.isEmpty());
        verify(agendamentoDAO, never())
                .existeConflitoHorario(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void calcularHorariosLivres_deveRetornarHorariosSemConflito() throws Exception {
        Disponibilidade disp = mock(Disponibilidade.class);
        ProfissionalSaude profissional = mock(ProfissionalSaude.class);

        LocalDate dataAlvo = LocalDate.of(2025, 1, 6);
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fim = LocalTime.of(9, 0);
        int duracaoMinutos = 30;

        when(disp.getDiaSemana()).thenReturn(dataAlvo.getDayOfWeek());
        when(disp.getHoraInicio()).thenReturn(inicio);
        when(disp.getHoraFim()).thenReturn(fim);
        when(disp.getProfissional()).thenReturn(profissional);
        when(profissional.getId()).thenReturn(1);

        when(agendamentoDAO.existeConflitoHorario(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);

        List<LocalTime> horarios = agendamentoBO.calcularHorariosLivres(disp, dataAlvo, duracaoMinutos);

        assertEquals(Arrays.asList(
                LocalTime.of(8, 0),
                LocalTime.of(8, 30)
        ), horarios);
    }

    // Testes de busca

    @Test
    public void listarTodos_deveDelegarParaDAO() throws Exception {
        Agendamento ag1 = mock(Agendamento.class);
        Agendamento ag2 = mock(Agendamento.class);
        when(agendamentoDAO.listarTodos()).thenReturn(Arrays.asList(ag1, ag2));

        List<Agendamento> resultado = agendamentoBO.listarTodos();

        assertEquals(2, resultado.size());
        verify(agendamentoDAO, times(1)).listarTodos();
    }

    @Test
    public void buscarPorId_deveRetornarAgendamentoQuandoEncontrado() throws Exception {
        Agendamento agendamento = mock(Agendamento.class);
        when(agendamentoDAO.buscarPorId(1)).thenReturn(agendamento);

        Agendamento resultado = agendamentoBO.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals(agendamento, resultado);
    }

    @Test(expected = NegocioException.class)
    public void buscarPorId_deveLancarExcecaoQuandoNaoEncontrado() throws Exception {
        when(agendamentoDAO.buscarPorId(99)).thenReturn(null);

        agendamentoBO.buscarPorId(99);
    }

    @Test
    public void buscarAgendamentosPorPaciente_deveRetornarListaQuandoExistemRegistros() throws Exception {
        Agendamento ag1 = mock(Agendamento.class);
        Agendamento ag2 = mock(Agendamento.class);
        when(agendamentoDAO.buscarAgendamentosPorPaciente(1))
                .thenReturn(Arrays.asList(ag1, ag2));

        List<Agendamento> resultado = agendamentoBO.buscarAgendamentosPorPaciente(1);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test(expected = NegocioException.class)
    public void buscarAgendamentosPorPaciente_deveLancarExcecaoQuandoListaVazia() throws Exception {
        when(agendamentoDAO.buscarAgendamentosPorPaciente(1))
                .thenReturn(Collections.emptyList());

        agendamentoBO.buscarAgendamentosPorPaciente(1);
    }
}
