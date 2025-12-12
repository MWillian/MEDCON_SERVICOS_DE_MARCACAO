package br.com.medcon.bo;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.EspecialidadeDAO;
import br.com.medcon.dao.TipoServicoDAO;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.TipoServico;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TipoServicoBOTest {

    private TipoServicoDAO daoMock;
    private EspecialidadeDAO espDaoMock;
    private TipoServicoBO bo;

    @Before
    public void setup() {
        daoMock = Mockito.mock(TipoServicoDAO.class);
        espDaoMock = Mockito.mock(EspecialidadeDAO.class);
        bo = new TipoServicoBO(daoMock, espDaoMock);
    }

    private TipoServico criarServicoValido() {
        TipoServico s = new TipoServico();
        s.setNome("Consulta");
        s.setDuracaoMinutos(30);

        Especialidade esp = new Especialidade();
        esp.setId(1);
        esp.setNome("Clínico Geral");
        s.setEspecialidadeNecessaria(esp);

        return s;
    }

    // SALVAR UMA ESPECIALIDADE COM SUCESSO
  
    @Test
    public void testSalvarComSucesso() throws Exception {
        TipoServico servico = criarServicoValido();

        when(espDaoMock.buscarPorId(1)).thenReturn(servico.getEspecialidadeNecessaria());
        when(daoMock.buscarPorNomeEEspecialidadeIgnoreCase("Consulta", 1)).thenReturn(null);

        bo.salvar(servico);

        verify(daoMock, times(1)).salvar(servico);
    }

    // ERRO DE NOME INVÁLIDO (POUCOS CARACTERES)
  
    @Test
    public void testSalvarNomeInvalido() throws Exception {
        TipoServico servico = criarServicoValido();
        servico.setNome("A"); // inválido

        try {
            bo.salvar(servico);
            fail("Era esperado lançar NegocioException");
        } catch (NegocioException e) {
            assertEquals("Erro: O nome do serviço deve ter ao menos 3 caracteres.", e.getMessage());
        }

        verify(daoMock, never()).salvar(any(TipoServico.class));
    }

