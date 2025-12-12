package br.com.medcon.bo;

import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.DisponibilidadeDAO;
import br.com.medcon.vo.Disponibilidade;
import br.com.medcon.vo.PostoSaude;
import br.com.medcon.vo.ProfissionalSaude;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DisponibilidadeBOTest {

    @Mock
    private DisponibilidadeDAO disponibilidadeDAO;

    private DisponibilidadeBO disponibilidadeBO;

    @Before
    public void setUp() {
        disponibilidadeBO = new DisponibilidadeBO(disponibilidadeDAO);
    }

    // TESTES DE SALVAR
    @Test
    public void salvar_DisponibilidadeComSucesso() throws NegocioException, SQLException {
        Disponibilidade novaDisp = criarDisponibilidadeValida();

        when(disponibilidadeDAO.buscarPorMedico(anyInt())).thenReturn(Collections.emptyList());

        disponibilidadeBO.salvar(novaDisp);

        verify(disponibilidadeDAO).salvar(novaDisp);
    }

    @Test(expected = NegocioException.class)
    public void salvar_LancarExcecaoQuandoHouverConflitoDeHorario() throws SQLException, NegocioException {
        Disponibilidade novaDisp = criarDisponibilidadeValida();

        novaDisp.setDiaSemana(java.time.DayOfWeek.MONDAY);
        novaDisp.setHoraInicio(LocalTime.of(10, 0));
        novaDisp.setHoraFim(LocalTime.of(12, 0));
        novaDisp.getPosto().setId(1);

        Disponibilidade existente = criarDisponibilidadeValida();
        existente.setDiaSemana(java.time.DayOfWeek.MONDAY);
        existente.setHoraInicio(LocalTime.of(11, 0));
        existente.setHoraFim(LocalTime.of(13, 0));
        existente.getPosto().setId(1);

        when(disponibilidadeDAO.buscarPorMedico(anyInt())).thenReturn(Arrays.asList(existente));

        disponibilidadeBO.salvar(novaDisp);

        verify(disponibilidadeDAO, never()).salvar(any());
    }

    // TESTE DE BUSCA
    @Test(expected = NegocioException.class)
    public void deveLancarExcecaoAoBuscarIdInexistente() throws SQLException, NegocioException {

        int idInexistente = 99;

        when(disponibilidadeDAO.buscarPorId(idInexistente)).thenReturn(null);

        disponibilidadeBO.buscarPorId(idInexistente);
    }

    private Disponibilidade criarDisponibilidadeValida() {
        Disponibilidade d = new Disponibilidade();

        ProfissionalSaude p = new ProfissionalSaude();
        p.setId(10);
        d.setProfissional(p);

        PostoSaude posto = new PostoSaude();
        posto.setId(5);
        d.setPosto(posto);

        d.setDiaSemana(java.time.DayOfWeek.MONDAY);
        d.setHoraInicio(LocalTime.of(8, 0));
        d.setHoraFim(LocalTime.of(12, 0));

        return d;
    }
}
