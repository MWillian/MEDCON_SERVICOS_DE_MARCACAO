package br.com.medcon.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.EspecialidadeDAO;
import br.com.medcon.dao.ProfissionalSaudeDAO;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.ProfissionalSaude;

public class ProfissionalSaudeBOTest {

    private ProfissionalSaudeDAO daoMock;
    private EspecialidadeDAO espDaoMock;
    private ProfissionalSaudeBO bo;

    @Before
    public void setup() {
        daoMock = Mockito.mock(ProfissionalSaudeDAO.class);
        espDaoMock = Mockito.mock(EspecialidadeDAO.class);
        bo = new ProfissionalSaudeBO(daoMock, espDaoMock);
    }

    private ProfissionalSaude criarProfissionalValido() {
        ProfissionalSaude p = new ProfissionalSaude();
        p.setNome("João Silva");
        p.setCpf("12345678901");
        p.setRegistroProfissional("CRM1234");

        Especialidade esp = new Especialidade();
        esp.setId(1);
        p.setEspecialidade(esp);

        return p;
    }

    @Test
    public void testSalvarComSucesso() throws Exception {
        ProfissionalSaude p = criarProfissionalValido();

        when(daoMock.buscarPorCpf("12345678901")).thenReturn(null);
        when(daoMock.buscarPorRegistroProfissional("CRM1234")).thenReturn(null);
        when(espDaoMock.buscarPorId(1)).thenReturn(new Especialidade());
        when(daoMock.buscarIdPessoaPorCpf("12345678901")).thenReturn(0);

        bo.salvar(p);

        verify(daoMock, times(1)).salvar(p);
    }

    @Test
    public void testSalvarComCpfDuplicado() throws Exception {
        ProfissionalSaude p = criarProfissionalValido();

        when(daoMock.buscarPorCpf("12345678901")).thenReturn(new ProfissionalSaude());

        try {
            bo.salvar(p);
            fail("Era esperado lançar NegocioException");
        } catch (NegocioException e) {
            assertEquals("Erro: Este CPF já está cadastrado como profissional de saúde.", e.getMessage());
        }

        verify(daoMock, never()).salvar(any(ProfissionalSaude.class));
    }
    
    @Test
    public void testSalvarEspecialidadeInexistente() throws Exception {
        ProfissionalSaude p = criarProfissionalValido();

        when(daoMock.buscarPorCpf("12345678901")).thenReturn(null);
        when(daoMock.buscarPorRegistroProfissional("CRM1234")).thenReturn(null);
        when(espDaoMock.buscarPorId(1)).thenReturn(null);

        try {
            bo.salvar(p);
            fail("Era esperado lançar NegocioException");
        } catch (NegocioException e) {
            assertEquals("Erro: Especialidade não encontrada no banco de dados.", e.getMessage());
        }

        verify(daoMock, never()).salvar(any(ProfissionalSaude.class));
    }
}
