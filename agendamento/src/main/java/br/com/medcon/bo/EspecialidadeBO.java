package br.com.medcon.bo;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.EspecialidadeDAO;
import br.com.medcon.vo.Especialidade;

public class EspecialidadeBO {
    private final EspecialidadeDAO especialidadeDAO;

    public EspecialidadeBO(EspecialidadeDAO especialidadeDAO) {
        this.especialidadeDAO = especialidadeDAO;
    }

    public void salvar(Especialidade especialidade) throws NegocioException, SQLException {
        ValidarCamposObrigatorios(especialidade);
        ValidarUnicidade(especialidade);
        especialidadeDAO.salvar(especialidade);
    }

    public List<Especialidade> listarTodos() throws SQLException {
        return especialidadeDAO.listarTodos();
    }

    public Especialidade buscarPorId(int id) throws NegocioException,SQLException {
        Especialidade esp = especialidadeDAO.buscarPorId(id);

        if (esp == null) {
            throw new NegocioException("Especialidade com ID " + id + " não encontrada.");
        }
        return esp;
    }

    public Especialidade buscarPorNome(String nome) throws SQLException {
        return especialidadeDAO.buscarPorNome(nome);
    }

    private void ValidarCamposObrigatorios(Especialidade especialidade) throws NegocioException {
        if (especialidade.getNome() == null || especialidade.getNome().trim().length() < 3) {
            throw new NegocioException("O nome da especialidade é obrigatório e deve ter ao menos 3 caracteres.");
        }
        if (especialidade.getDescricao() == null || especialidade.getDescricao().trim().length() < 10) {
            throw new NegocioException("A descriçao da especidade deve ser de no mínimo 10 caracteres.");
        }
    }

    private void ValidarUnicidade(Especialidade especialidade) throws NegocioException, SQLException {
        Especialidade existente = especialidadeDAO.buscarPorNome(especialidade.getNome());
        if (existente != null) {
            throw new NegocioException("Essa especialidade já está cadastrada no banco de dados.");
        }
    }
}
