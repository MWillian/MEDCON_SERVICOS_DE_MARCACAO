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
        ValidarUnicidade(paciente);
        dao.salvar(paciente);
    }

    //Métodos Auxiliares
    private void sanitizarDados(Paciente p) {
        p.setCpf(limparNumero(p.getCpf()));
        p.setTelefone(limparNumero(p.getTelefone()));
        p.setCartaoSus(limparNumero(p.getCartaoSus()));
    }

    private void validarCamposObrigatorios(Paciente p) throws NegocioException {
        ValidarNome(p.getNome());
        ValidarCpf(p.getCpf()); 
        ValidarDataNascimento(p.getDataNascimento());
        ValidarTelefone(p.getTelefone());
        ValidarEndereco(p.getEndereco());
        ValidarCartaoSus(p.getCartaoSus());
    }

    public void ValidarUnicidade(Paciente p) throws NegocioException, SQLException {
        Paciente existente = dao.buscarPorCpf(p.getCpf());
        if (existente != null) {
            throw new NegocioException("Já existe um paciente cadastrado com o CPF informado: " + p.getCpf());
        }
    }

    public void ValidarNome(String nome) throws NegocioException {
        if (nome == null || nome.trim().length() < 3) {
            throw new NegocioException("Nome do paciente inválido: deve conter ao menos 3 letras.");
        }
        if (nome != null && nome.matches(".*\\d.*")) {
        throw new NegocioException("O nome do paciente não pode conter números. Digite apenas letras.");
    }
    }

    public void ValidarCpf(String cpfLimpo) throws NegocioException {
        if (cpfLimpo == null || cpfLimpo.trim().isEmpty()) {
            throw new NegocioException("O CPF é obrigatório.");
        }
        if (cpfLimpo.length() != 11) {
            throw new NegocioException("CPF inválido: Deve conter exatamente 11 dígitos numéricos.");
        }
    }

    public void ValidarEndereco(String endereco) throws NegocioException {
        if (endereco == null || endereco.trim().length() < 5) {
            throw new NegocioException("Endereço muito curto. Informe logradouro e número.");
        }
    }

    public void ValidarDataNascimento(LocalDate data) throws NegocioException {
        if (data == null) {
            throw new NegocioException("A data de nascimento é obrigatória.");
        }
        if (data.isAfter(LocalDate.now())) {
            throw new NegocioException("Data de nascimento inválida: O paciente não pode ter nascido no futuro.");
        }
    }

    public void ValidarCartaoSus(String susLimpo) throws NegocioException {
        if (susLimpo == null) return;
        if (susLimpo.length() < 3) { 
            throw new NegocioException("Número do Cartão SUS inválido.");
        }
    }

    public String limparNumero(String texto) {
            if (texto == null) return "";
            return texto.replaceAll("\\D", "");
    }

    public Paciente buscarPorCpf(String cpf) throws SQLException, NegocioException {
        String cpfLimpo = limparNumero(cpf);
        
        if (!cpf.matches("\\d{11}")) {
            throw new NegocioException("CPF inválido! O CPF deve conter 11 dígitos numéricos.");
        }
        
        Paciente p = dao.buscarPorCpf(cpfLimpo);

        if (p == null) {
            throw new NegocioException("CPF não encontrado no sistema.");
        }

        return p;
    }

    public void ValidarTelefone(String foneLimpo) throws NegocioException {
        if (foneLimpo == null) return; 
        if (foneLimpo.length() < 10 || foneLimpo.length() > 11) {
            throw new NegocioException("Telefone inválido: Deve conter DDD + Número (10 ou 11 dígitos).");
        }
    }

    private void processarSalvamento(Paciente p) throws NegocioException, SQLException {
        Paciente pacienteExistente = dao.buscarPorCpf(p.getCpf());
        if (pacienteExistente != null) {
            throw new NegocioException("Este CPF já está cadastrado como PACIENTE.");
        }

        int idPessoaExistente = dao.buscarIdPessoaPorCpf(p.getCpf());

        if (idPessoaExistente > 0) {
            p.setId(idPessoaExistente); 
            dao.salvarApenasVinculo(p);
        } else {
            dao.salvar(p);
        }
    }
}
