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
        validarEspecialidade(profissional);
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

    public void ValidarCpf(String cpf) throws NegocioException{
        if((cpf) == null || cpf.trim().isEmpty()) {
            throw new NegocioException("Erro: O CPF é obrigatório.");
        }

        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        if (cpfLimpo.length() != 11) {
            throw new NegocioException("Erro: O CPF deve ter exatamente 11 dígitos.");
        }

        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            throw new NegocioException("Erro: CPF inválido (sequência repetida).");
        }
    }

    public void ValidarNome(String nome)throws NegocioException{
         if (nome == null || nome.trim().isEmpty()) {
            throw new NegocioException("Erro: O nome do profissional é obrigatório.");
        }

        if (nome.trim().length() < 3) {
            throw new NegocioException("Erro: O nome do profissional deve ter ao menos 3 caracteres.");
        }
    }

    public void ValidarTelefone(String telefone) throws NegocioException {
        String foneLimpo = limparNumero(telefone);
        
        if (foneLimpo.isEmpty()) {
            throw new NegocioException("Erro: O telefone é obrigatório.");
        }

        if (foneLimpo.length() < 10 || foneLimpo.length() > 11) {
            throw new NegocioException("Erro: Telefone inválido. Deve conter DDD + número (10 ou 11 dígitos).");
        }
    }

     public void ValidarEndereco(String endereco) throws NegocioException {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new NegocioException("Erro: O endereço é obrigatório.");
        }

        if (endereco.trim().length() < 10) {
            throw new NegocioException("Erro: O endereço deve ter ao menos 10 caracteres.");
        }
    }

    public void ValidarRegistro(String registro) throws NegocioException {
        if (registro == null
                || registro.trim().isEmpty()) {
            throw new NegocioException("Erro: O registro profissional (CRM/COREN/etc.) é obrigatório.");
        }

        if (registro.trim().length() < 4) {
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

    public void validarUnicidadeCpf(String cpf) throws NegocioException, SQLException {
        ProfissionalSaude existente = dao.buscarPorCpf(cpf);
        if (existente != null) {
            throw new NegocioException("Erro: Este CPF já está cadastrado como profissional de saúde.");
        }
    }

    public void validarUnicidadeRegistro(String registro) throws NegocioException, SQLException {
        ProfissionalSaude existente = dao.buscarPorRegistroProfissional(registro);
        if (existente != null) {
            throw new NegocioException("Erro: Este registro profissional já está cadastrado.");
        }
    }

     public String limparNumero(String texto) {
        if (texto == null) return "";
        return texto.replaceAll("\\D", "");
    }
}
