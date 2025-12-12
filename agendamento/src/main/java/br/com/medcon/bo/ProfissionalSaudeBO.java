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
        validarCamposObrigatorios(profissional);
        validarRegistro(profissional);
        validarEspecialidade(profissional);
        validarUnicidadeCpf(profissional);
        validarUnicidadeRegistro(profissional);

        try {
            int idPessoaExistente = dao.buscarIdPessoaPorCpf(profissional.getCpf());

            if (idPessoaExistente > 0) {
                profissional.setId(idPessoaExistente);
                dao.salvarApenasVinculo(profissional);
            } else {
                dao.salvar(profissional);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed: tb_pessoa.cpf")) {
                throw new NegocioException("Erro: Este CPF já está cadastrado no sistema.");
            } else if (e.getMessage().contains("UNIQUE constraint failed: tb_profissional.registro_profissional")) {
                throw new NegocioException("Erro: Este registro profissional já está cadastrado.");
            }
            throw e;
        }
    }

    public List<ProfissionalSaude> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public ProfissionalSaude buscarPorId(int id) throws SQLException, NegocioException {
        ProfissionalSaude p = dao.buscarPorId(id);
        if (p == null) {
            throw new NegocioException("Profissional com ID " + id + " não encontrado.");
        }
        return p;
    }

    private void validarCamposObrigatorios(ProfissionalSaude profissional) throws NegocioException {
        if (profissional.getNome() == null || profissional.getNome().trim().isEmpty()) {
            throw new NegocioException("Erro: O nome do profissional é obrigatório.");
        }

        if (profissional.getNome().trim().length() < 3) {
            throw new NegocioException("Erro: O nome do profissional deve ter ao menos 3 caracteres.");
        }

        if (profissional.getCpf() == null || profissional.getCpf().trim().isEmpty()) {
            throw new NegocioException("Erro: O CPF é obrigatório.");
        }

        String cpfLimpo = profissional.getCpf().replaceAll("[^0-9]", "");

        if (cpfLimpo.length() != 11) {
            throw new NegocioException("Erro: O CPF deve ter exatamente 11 dígitos.");
        }

        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            throw new NegocioException("Erro: CPF inválido (sequência repetida).");
        }
    }

    private void validarRegistro(ProfissionalSaude profissional) throws NegocioException {
        if (profissional.getRegistroProfissional() == null
                || profissional.getRegistroProfissional().trim().isEmpty()) {
            throw new NegocioException("Erro: O registro profissional (CRM/COREN/etc.) é obrigatório.");
        }

        if (profissional.getRegistroProfissional().trim().length() < 4) {
            throw new NegocioException("Erro: O registro profissional deve ter ao menos 4 caracteres.");
        }
    }

    private void validarEspecialidade(ProfissionalSaude profissional) throws NegocioException, SQLException {
        if (profissional.getEspecialidade() == null || profissional.getEspecialidade().getId() <= 0) {
            throw new NegocioException("Erro: Selecione uma especialidade válida.");
        }

        Especialidade esp = especialidadeDAO.buscarPorId(profissional.getEspecialidade().getId());
        if (esp == null) {
            throw new NegocioException("Erro: Especialidade não encontrada no banco de dados.");
        }
    }

    private void validarUnicidadeCpf(ProfissionalSaude profissional) throws NegocioException, SQLException {
        ProfissionalSaude existente = dao.buscarPorCpf(profissional.getCpf());
        if (existente != null) {
            throw new NegocioException("Erro: Este CPF já está cadastrado como profissional de saúde.");
        }
    }

    private void validarUnicidadeRegistro(ProfissionalSaude profissional) throws NegocioException, SQLException {
        ProfissionalSaude existente = dao.buscarPorRegistroProfissional(profissional.getRegistroProfissional());
        if (existente != null) {
            throw new NegocioException("Erro: Este registro profissional já está cadastrado.");
        }
    }
}
