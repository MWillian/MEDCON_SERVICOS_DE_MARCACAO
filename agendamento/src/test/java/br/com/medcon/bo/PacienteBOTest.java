package br.com.medcon.bo;

import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.time.LocalDate;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.PacienteDAO;
import br.com.medcon.vo.Paciente;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PacienteBOTest {

    @Mock
    private PacienteDAO pacienteDAO;

    private PacienteBO pacienteBO;

    @Before
    public void setup() {
        pacienteBO = new PacienteBO(pacienteDAO);
    }

    // TESTE PARA SALVAR (SE O CPF É INEXISTENTE)
    @Test
    public void deveSalvarPacienteQuandoCpfNaoExiste() throws Exception {
        Paciente p = criarPacienteValido();

        when(pacienteDAO.buscarPorCpf("12345678901")).thenReturn(null);
        when(pacienteDAO.buscarIdPessoaPorCpf("12345678901")).thenReturn(0);

        pacienteBO.salvar(p);

        verify(pacienteDAO, times(1)).salvar(p);
        verify(pacienteDAO, never()).salvarApenasVinculo(any());
    }


  // TESTE PARA BUSCAR CPF 
  
    @Test(expected = NegocioException.class)
    public void deveLancarExcecaoQuandoCpfNaoEncontrado() throws Exception {
        when(pacienteDAO.buscarPorCpf("12345678901")).thenReturn(null);

        pacienteBO.buscarPorCpf("123.456.789-01");
    }

    // TESTE PARA VERIFICAR CPF INVALIDO
  
    @Test(expected = NegocioException.class)
    public void deveLancarExcecaoQuandoCpfForInvalido() throws Exception {
        pacienteBO.validarCpf("12345"); 
    }


    private Paciente criarPacienteValido() {
        Paciente p = new Paciente();
        p.setNome("João da Silva");
        p.setCpf("12345678901");
        p.setTelefone("11987654321");
        p.setEndereco("Rua XPTO, 123 - Bairro");
        p.setDataNascimento(LocalDate.of(1990, 1, 10));
        p.setCartaoSus("12345678901");

        return p;
    }
}
