package br.com.medcon.bo;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.ProfissionalPostoDAO;
import br.com.medcon.vo.PostoSaude;
import br.com.medcon.vo.ProfissionalPosto;

public class ProfissionalPostoBO {
    private final ProfissionalPostoDAO profissionalPostoDAO;

    public ProfissionalPostoBO(ProfissionalPostoDAO profissionalPostoDAO) {
        this.profissionalPostoDAO = profissionalPostoDAO;
    }

    public void vincular(int idProfissional, int idPosto) throws NegocioException, SQLException {
        validarIds(idProfissional, idPosto);
        validarVinculoDuplicado(idProfissional, idPosto);
        profissionalPostoDAO.vincular(idProfissional, idPosto);
    }

    public void desvincular(int idProfissional, int idPosto) throws NegocioException, SQLException {
        validarIds(idProfissional, idPosto);
        profissionalPostoDAO.desvincular(idProfissional, idPosto);
    }

    public List<PostoSaude> listarPostosDoProfissional(int idProfissional) throws NegocioException, SQLException {
        if (idProfissional <= 0) {
            throw new NegocioException("Erro: ID do profissional inválido.");
        }
        return profissionalPostoDAO.listarPostosDoProfissional(idProfissional);
    }

    public List<ProfissionalPosto> listarTodos() throws SQLException {
        return profissionalPostoDAO.listarTodos();
    }
    
    private void validarIds(int idProfissional, int idPosto) throws NegocioException {
        if (idProfissional <= 0) {
            throw new NegocioException("Erro: ID do profissional é inválido.");
        }
        if (idPosto <= 0) {
            throw new NegocioException("Erro: ID do posto de saúde é inválido.");
        }
    }

    private void validarVinculoDuplicado(int idProfissional, int idPosto) throws NegocioException, SQLException {
        boolean existe = profissionalPostoDAO.verificarVinculoExistente(idProfissional, idPosto);
        if (existe) {
            throw new NegocioException("Erro: Este profissional já está vinculado a este posto de saúde.");
        }
    }
}
