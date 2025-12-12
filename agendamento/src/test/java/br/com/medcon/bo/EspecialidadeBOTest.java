package br.com.medcon.bo;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Collections;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.EspecialidadeDAO;
import br.com.medcon.vo.Especialidade;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EspecialidadeBOTest {

    @Mock
    private EspecialidadeDAO especialidadeDAO;

    private EspecialidadeBO especialidadeBO;

    @Before
    public void setUp() {
        especialidadeBO = new EspecialidadeBO(especialidadeDAO);
    }

    // TESTES DE SALVAR
  
    @Test
    public void deveSalvarEspecialidadeComSucesso() throws Exception {
        Especialidade esp = criarEspecialidadeValida();

        when(especialidadeDAO.buscarPorNomeIgnoreCase(anyString()))
                .thenReturn(null);
      
        especialidadeBO.salvar(esp);

        verify(especialidadeDAO, times(1)).salvar(esp);
    }

      // TESTE DE ERRO (NOME JÁ EXISTENTE)
  
    @Test(expected = NegocioException.class)
    public void deveLancarExcecaoQuandoNomeJaExistir() throws Exception {
        Especialidade nova = criarEspecialidadeValida();
        Especialidade existente = criarEspecialidadeValida();
        existente.setId(5);

        when(especialidadeDAO.buscarPorNomeIgnoreCase("Cardiologia")).thenReturn(existente);

        especialidadeBO.salvar(nova);
    }

      // TESTE DE ERRO (ID INEXISTENTE)
    
  @Test(expected = NegocioException.class)
    public void deveLancarExcecaoAoBuscarIdInexistente() throws Exception {
        int id = 10;

        when(especialidadeDAO.buscarPorId(id)).thenReturn(null);

        especialidadeBO.buscarPorId(id);
    }

    private Especialidade criarEspecialidadeValida() {
        Especialidade e = new Especialidade();
        e.setId(1);
        e.setNome("Cardiologia");
        e.setDescricao("Especialidade relacionada ao coração.");
        return e;
    }
}

