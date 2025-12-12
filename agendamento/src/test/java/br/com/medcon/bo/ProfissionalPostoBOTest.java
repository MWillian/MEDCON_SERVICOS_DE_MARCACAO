package br.com.medcon.bo;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.ProfissionalPostoDAO;
import br.com.medcon.vo.PostoSaude;
import br.com.medcon.vo.ProfissionalPosto;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ProfissionalPostoBOTest {

    private ProfissionalPostoDAO daoMock;
    private ProfissionalPostoBO bo;

    @Before
    public void setup() {
        daoMock = Mockito.mock(ProfissionalPostoDAO.class);
        bo = new ProfissionalPostoBO(daoMock);
    }

    // TESTE QUE VERIFICA VINCULO BEM SUCEDIDO
  
    @Test
    public void testVincularComSucesso() throws Exception {
        int idProf = 1;
        int idPosto = 10;

        when(daoMock.verificarVinculoExistente(idProf, idPosto)).thenReturn(false);

        bo.vincular(idProf, idPosto);

        verify(daoMock, times(1)).vincular(idProf, idPosto);
    }

    // ERRO SE OCORRER VINCULO DUPLICADO
  
    @Test(expected = NegocioException.class)
    public void testVincularDuplicado() throws Exception {
        int idProf = 1;
        int idPosto = 10;

        when(daoMock.verificarVinculoExistente(idProf, idPosto)).thenReturn(true);

        bo.vincular(idProf, idPosto);
    }

    // ERRO AO ENCONTRAR ID INVÁLDO AO LISTAR
    @Test(expected = NegocioException.class)
    public void testListarPostosComIdInvalido() throws Exception {
        bo.listarPostosDoProfissional(0);
    }
}
