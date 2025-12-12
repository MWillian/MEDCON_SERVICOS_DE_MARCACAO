package br.com.medcon.bo;

import java.sql.SQLException;
import java.time.LocalDate;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.PacienteDAO;
import br.com.medcon.vo.Paciente;

public class PacienteBO {
    private final PacienteDAO dao;

    public PacienteBO(PacienteDAO dao) {
        this.dao = dao;
    }

    public void salvar(Paciente paciente) throws NegocioException, SQLException {
        sanitizarDados(paciente);
        validarCamposObrigatorios(paciente);
        validarUnicidade(paciente);

        try {
            int idPessoaExistente = dao.buscarIdPessoaPorCpf(paciente.getCpf());

            if (idPessoaExistente > 0) {
                paciente.setId(idPessoaExistente);
                dao.salvarApenasVinculo(paciente);
            } else {
                dao.salvar(paciente);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed: tb_pessoa.cpf")) {
                throw new NegocioException("Erro: Este CPF já está cadastrado no sistema.");
            }
            throw e;
        }
    }

    public Paciente buscarPorCpf(String cpf) throws SQLException, NegocioException {
        String cpfLimpo = limparNumero(cpf);

        // Valida formato antes de buscar no banco
        if (cpfLimpo.length() != 11) {
            throw new NegocioException("Erro: CPF inválido. O CPF deve conter exatamente 11 dígitos numéricos.");
        }

        Paciente p = dao.buscarPorCpf(cpfLimpo);

        if (p == null) {
            throw new NegocioException("Erro: CPF não encontrado no sistema.");
        }

        return p;
    }

    public void validarNome(String nome) throws NegocioException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NegocioException("Erro: O nome do paciente é obrigatório.");
        }

        if (nome.trim().length() < 3) {
            throw new NegocioException("Erro: O nome do paciente deve ter ao menos 3 caracteres.");
        }

        if (nome.matches(".*\\d.*")) {
            throw new NegocioException("Erro: Nome do paciente inválido: deve conter ao menos 3 letras, sem números.");
        }
    }

    public void validarCpf(String cpf) throws NegocioException {
        String cpfLimpo = limparNumero(cpf);
        
        if (cpfLimpo.isEmpty()) {
            throw new NegocioException("Erro: O CPF é obrigatório.");
        }

        if (cpfLimpo.length() != 11) {
            throw new NegocioException("Erro: CPF inválido. Deve conter exatamente 11 dígitos numéricos.");
        }

        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            throw new NegocioException("Erro: CPF inválido (sequência repetida).");
        }
    }

    public void validarDataNascimento(LocalDate data) throws NegocioException {
        if (data == null) {
            throw new NegocioException("Erro: A data de nascimento é obrigatória.");
        }

        if (data.isAfter(LocalDate.now())) {
            throw new NegocioException("Erro: Data de nascimento inválida. O paciente não pode ter nascido no futuro.");
        }

        if (data.isAfter(LocalDate.now().minusYears(1))) {
            throw new NegocioException("Erro: O paciente deve ter pelo menos 1 ano de idade.");
        }
    }

    public void validarTelefone(String telefone) throws NegocioException {
        String foneLimpo = limparNumero(telefone);
        
        if (foneLimpo.isEmpty()) {
            throw new NegocioException("Erro: O telefone é obrigatório.");
        }

        if (foneLimpo.length() < 10 || foneLimpo.length() > 11) {
            throw new NegocioException("Erro: Telefone inválido. Deve conter DDD + número (10 ou 11 dígitos).");
        }
    }

    public void validarEndereco(String endereco) throws NegocioException {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new NegocioException("Erro: O endereço é obrigatório.");
        }

        if (endereco.trim().length() < 10) {
            throw new NegocioException("Erro: O endereço deve ter ao menos 10 caracteres.");
        }
    }

    public void validarCartaoSus(String cartaoSus) throws NegocioException {
        String susLimpo = limparNumero(cartaoSus);
        
        if (susLimpo.isEmpty()) {
            throw new NegocioException("Erro: O Cartão SUS é obrigatório.");
        }

        if (susLimpo.length() != 11 && susLimpo.length() != 15) {
            throw new NegocioException("Erro: Cartão SUS inválido. Deve conter 11 ou 15 dígitos.");
        }
    }

    public String limparNumero(String texto) {
        if (texto == null) return "";
        return texto.replaceAll("\\D", "");
    }
    
    private void sanitizarDados(Paciente p) {
        p.setCpf(limparNumero(p.getCpf()));
        p.setTelefone(limparNumero(p.getTelefone()));
        p.setCartaoSus(limparNumero(p.getCartaoSus()));
    }

    private void validarCamposObrigatorios(Paciente p) throws NegocioException {
        validarNome(p.getNome());
        validarCpf(p.getCpf());
        validarDataNascimento(p.getDataNascimento());
        validarTelefone(p.getTelefone());
        validarEndereco(p.getEndereco());
        validarCartaoSus(p.getCartaoSus());
    }

    private void validarUnicidade(Paciente p) throws NegocioException, SQLException {
        // Verifica se já existe um PACIENTE com este CPF
        Paciente existente = dao.buscarPorCpf(p.getCpf());
        if (existente != null) {
            throw new NegocioException("Erro: Já existe um paciente cadastrado com o CPF informado.");
        }
    }
}
