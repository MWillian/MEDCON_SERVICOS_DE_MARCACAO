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
        validarCamposObrigatorios(posto);
        validarUnicidade(posto);
        dao.salvar(posto);
    }

    public List<PostoSaude> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public PostoSaude buscarPorId(int id) throws SQLException, NegocioException {
        PostoSaude posto = dao.buscarPorId(id);
        if (posto == null) {
            throw new NegocioException("Posto com ID " + id + " não encontrado.");
        }
        return posto;
    }

    private void validarCamposObrigatorios(PostoSaude posto) throws NegocioException {
        validarNome(posto.getNome());
        validarEndereco(posto.getEndereco());
        validarTelefone(posto.getTelefone());
    }

    private void validarNome(String nome) throws NegocioException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NegocioException("Erro: O nome do posto é obrigatório.");
        }

        if (nome.trim().length() < 5) {
            throw new NegocioException("Erro: O nome do posto deve ter ao menos 5 caracteres.");
        }

        if (nome.matches(".*\\d.*")) {
            throw new NegocioException("Erro: O nome do posto não pode conter números.");
        }
    }

    private void validarEndereco(String endereco) throws NegocioException {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new NegocioException("Erro: O endereço do posto é obrigatório.");
        }

        if (endereco.trim().length() < 10) {
            throw new NegocioException("Erro: O endereço do posto deve ter ao menos 10 caracteres.");
        }
    }

    private void validarTelefone(String telefone) throws NegocioException {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new NegocioException("Erro: O telefone do posto é obrigatório.");
        }

        String telefoneLimpo = telefone.replaceAll("[^0-9]", "");

        if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
            throw new NegocioException("Erro: O telefone deve ter 10 ou 11 dígitos (com DDD).");
        }

        if (telefone.matches(".*[a-zA-Z].*")) {
            throw new NegocioException("Erro: O telefone não pode conter letras.");
        }
    }

    private void validarUnicidade(PostoSaude posto) throws NegocioException, SQLException {
        PostoSaude existente = dao.buscarPorNomeEnderecoTelefoneIgnoreCase(
                posto.getNome(),
                posto.getEndereco(),
                posto.getTelefone());

        if (existente != null) {
            throw new NegocioException(
                    "Erro: Já existe um posto cadastrado com este nome, endereço e telefone.");
        }
    }
}
