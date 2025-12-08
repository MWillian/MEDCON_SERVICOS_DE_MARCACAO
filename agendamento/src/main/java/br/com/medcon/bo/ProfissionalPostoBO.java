package br.com.medcon.bo;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.dao.ProfissionalPostoDAO;
import br.com.medcon.vo.PostoSaude;
import br.com.medcon.vo.ProfissionalPosto;

public class ProfissionalPostoBO {
    private final ProfissionalPostoDAO profissionalPostoDAO;

    public ProfissionalPostoBO(ProfissionalPostoDAO profissionalPostoDAO) {
        this.profissionalPostoDAO = profissionalPostoDAO;
    }

    public List<PostoSaude> listarPostosDoProfissional(int idProfissional) throws SQLException {
        return profissionalPostoDAO.listarPostosDoProfissional(idProfissional);
    }

    public List<ProfissionalPosto> listarTodos() throws SQLException {
        return profissionalPostoDAO.listarTodos();
    }
}
