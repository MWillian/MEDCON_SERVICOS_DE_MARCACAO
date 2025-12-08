package br.com.medcon.bo;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.dao.PostoSaudeDAO;
import br.com.medcon.vo.PostoSaude;

public class PostoSaudeBO {
    private final PostoSaudeDAO postoSaudeDAO;

    public PostoSaudeBO(PostoSaudeDAO postoSaudeDAO) {
        this.postoSaudeDAO = postoSaudeDAO;
    }

    public void salvar(PostoSaude ps) throws SQLException {
        postoSaudeDAO.salvar(ps);
    };
    public void atualizar(PostoSaude ps) throws SQLException {
        postoSaudeDAO.atualizar(ps);
    };
    public void deletar(int id) throws SQLException {
        postoSaudeDAO.deletar(id);
    };
    public PostoSaude buscarPorId(int id) throws SQLException{ 
        return postoSaudeDAO.buscarPorId(id);
    };
    public List<PostoSaude> listarTodos() throws SQLException { 
        return postoSaudeDAO.listarTodos();
    };
}
