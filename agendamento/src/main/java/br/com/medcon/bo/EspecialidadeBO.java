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
        validarCamposObrigatorios(especialidade);
        validarUnicidade(especialidade);
        especialidadeDAO.salvar(especialidade);
    }

    public List<Especialidade> listarTodos() throws SQLException {
        return especialidadeDAO.listarTodos();
    }

    public Especialidade buscarPorId(int id) throws NegocioException, SQLException {
        Especialidade esp = especialidadeDAO.buscarPorId(id);

        if (esp == null) {
            throw new NegocioException("Especialidade com ID " + id + " não encontrada.");
        }
        return esp;
    }

    public Especialidade buscarPorNome(String nome) throws SQLException {
        return especialidadeDAO.buscarPorNome(nome);
    }

    private void validarCamposObrigatorios(Especialidade especialidade) throws NegocioException {
        String nome = especialidade.getNome();
        String descricao = especialidade.getDescricao();

        if (nome == null || nome.trim().isEmpty()) {
            throw new NegocioException("Erro: O nome da especialidade é obrigatório.");
        }

        if (nome.trim().length() < 3) {
            throw new NegocioException("Erro: O nome da especialidade deve ter ao menos 3 caracteres.");
        }

        if (nome.matches(".*\\d.*")) {
            throw new NegocioException("Erro: O nome da especialidade não pode conter números.");
        }

        if (descricao == null || descricao.trim().isEmpty()) {
            throw new NegocioException("Erro: A descrição da especialidade é obrigatória.");
        }

        if (descricao.trim().length() < 10) {
            throw new NegocioException("Erro: A descrição da especialidade deve ter ao menos 10 caracteres.");
        }

        if (descricao.matches(".*\\d.*")) {
            throw new NegocioException("Erro: A descrição da especialidade não pode conter números.");
        }
    }

    private void validarUnicidade(Especialidade especialidade) throws NegocioException, SQLException {
        Especialidade existente = especialidadeDAO.buscarPorNomeIgnoreCase(especialidade.getNome());
        
        if (existente != null) {
            throw new NegocioException("Erro: Já existe uma especialidade cadastrada com este nome.");
        }
    }
}
