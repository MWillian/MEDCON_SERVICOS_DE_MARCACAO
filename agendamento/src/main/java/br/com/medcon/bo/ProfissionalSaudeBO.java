package br.com.medcon.bo;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.EspecialidadeDAO;
import br.com.medcon.dao.ProfissionalSaudeDAO;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.ProfissionalSaude;

public class ProfissionalSaudeBO {

    private final ProfissionalSaudeDAO dao;
    private final EspecialidadeDAO especialidadeDAO;

    public ProfissionalSaudeBO(ProfissionalSaudeDAO dao, EspecialidadeDAO especialidadeDAO) {
        this.dao = dao;
        this.especialidadeDAO = especialidadeDAO;
    }
    
    public void salvar(ProfissionalSaude profissional) throws NegocioException, SQLException {
        ValidarCamposObrigatorios(profissional);
        ValidarRegistro(profissional);
        ValidarEspecialidade(profissional);

        dao.salvar(profissional);
    }

    public List<ProfissionalSaude> listarTodos() throws SQLException {
        return dao.listarTodos();
    }
    
    public ProfissionalSaude buscarPorId(int id) throws SQLException, NegocioException {
        ProfissionalSaude p = dao.buscarPorId(id);
        if (p == null) throw new NegocioException("Profissional não encontrado.");
        return p;
    }

    private void ValidarCamposObrigatorios(ProfissionalSaude profissional) throws NegocioException{
        if (profissional.getNome() == null || profissional.getNome().trim().length() < 3) {
            throw new NegocioException("Nome é obrigatório.");
        }
        if (profissional.getCpf() == null || profissional.getCpf().length() != 11) {
            throw new NegocioException("CPF inválido (deve ter 11 dígitos).");
        }
    }
    
    private void ValidarRegistro(ProfissionalSaude profissional)throws NegocioException{
        if (profissional.getRegistroProfissional() == null || profissional.getRegistroProfissional().isEmpty()) {
            throw new NegocioException("Registro Profissional (CRM/COREN) é obrigatório.");
        }
    }

    private void ValidarEspecialidade(ProfissionalSaude profissional) throws NegocioException, SQLException{
        if (profissional.getEspecialidade() == null || profissional.getEspecialidade().getId() <= 0) {
            throw new NegocioException("Selecione uma especialidade válida.");
        }

        Especialidade esp = especialidadeDAO.buscarPorId(profissional.getEspecialidade().getId());
        if (esp == null) {
            throw new NegocioException("Especialidade não encontrada no banco.");
        }
    }
}