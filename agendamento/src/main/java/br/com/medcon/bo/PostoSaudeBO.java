package br.com.medcon.bo;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.PostoSaudeDAO;
import br.com.medcon.vo.PostoSaude;

public class PostoSaudeBO {

    private final PostoSaudeDAO dao;

    public PostoSaudeBO(PostoSaudeDAO dao) {
        this.dao = dao;
    }

    public void salvar(PostoSaude posto) throws NegocioException, SQLException {
        ValidarCamposObrigatorios(posto);
        dao.salvar(posto);
    }

    public List<PostoSaude> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public PostoSaude buscarPorId(int id) throws SQLException, NegocioException {
        PostoSaude posto = dao.buscarPorId(id); 
        if (posto == null) {
            throw new NegocioException("Posto não encontrado.");
        }
        return posto;
    }

    public void ValidarCamposObrigatorios(PostoSaude posto) throws NegocioException{
        if (posto.getNome() == null || posto.getNome().trim().length() < 10) {
            throw new NegocioException("Nome do Posto é obrigatório (min 10 caracteres).");
        }
        if (posto.getEndereco() == null || posto.getEndereco().trim().length() < 5) {
            throw new NegocioException("Endereço é obrigatório.");
        }
    }
}