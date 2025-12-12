package br.com.medcon.bo;

import static org.mockito.Mockito.*;

import java.sql.SQLException;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.PostoSaudeDAO;
import br.com.medcon.vo.PostoSaude;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PostoSaudeBOTest {

    @Mock
    private PostoSaudeDAO postoDAO;

    private PostoSaudeBO postoBO;

    @Before
    public void setUp() {
        postoBO = new PostoSaudeBO(postoDAO);
    }

    // TESTE DE SALVAR POSTO (SEM DUPLICIDADE)
    @Test
    public void deveSalvarPostoComSucesso() throws Exception {
        PostoSaude posto = criarPostoValido();

        when(postoDAO.buscarPorNomeEnderecoTelefoneIgnoreCase(
                posto.getNome(), posto.getEndereco(), posto.getTelefone()))
                .thenReturn(null); 
        postoBO.salvar(posto);

        verify(postoDAO, times(1)).salvar(posto);
    }

    // TESTE ERRO (POSTO JA EXISTE)
  
    @Test(expected = NegocioException.class)
    public void deveLancarExcecaoQuandoPostoJáExiste() throws Exception {
        PostoSaude novo = criarPostoValido();
        PostoSaude existente = criarPostoValido();
        existente.setId(99);

        when(postoDAO.buscarPorNomeEnderecoTelefoneIgnoreCase(
                novo.getNome(), novo.getEndereco(), novo.getTelefone()))
                .thenReturn(existente);

        postoBO.salvar(novo);
    }

    // TESTE ERRO (ID INEXISTENTE)
  
    @Test(expected = NegocioException.class)
    public void deveLancarExcecaoAoBuscarIdInexistente() throws Exception {
        int idInexistente = 50;

        when(postoDAO.buscarPorId(idInexistente)).thenReturn(null);

        postoBO.buscarPorId(idInexistente);
    }

  // METODO AUXILIAR
  
    private PostoSaude criarPostoValido() {
        PostoSaude p = new PostoSaude();
        p.setId(1);
        p.setNome("Posto Central");
        p.setEndereco("Rua das Flores, 123");
        p.setTelefone("11987654321");
        return p;
    }
}
