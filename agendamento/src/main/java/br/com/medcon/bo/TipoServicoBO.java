package br.com.medcon.bo;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.EspecialidadeDAO;
import br.com.medcon.dao.TipoServicoDAO;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.TipoServico;

public class TipoServicoBO {
    private final TipoServicoDAO dao;
    private final EspecialidadeDAO especialidadeDAO;

    public TipoServicoBO(TipoServicoDAO dao, EspecialidadeDAO especialidadeDAO) {
        this.dao = dao;
        this.especialidadeDAO = especialidadeDAO;
    }
    
    public void salvar(TipoServico servico) throws NegocioException, SQLException {
        validarCamposObrigatorios(servico);
        validarUnicidade(servico);
        dao.salvar(servico);
    }

    public List<TipoServico> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public TipoServico buscarPorId(int id) throws SQLException, NegocioException {
        if (id <= 0) {
            throw new NegocioException("Tipo do Serviço deve ter um ID válido.");
        }
        TipoServico servico = dao.buscarPorId(id);
        if (servico == null) {
            throw new NegocioException("Serviço com ID " + id + " não encontrado.");
        }
        return servico;
    }

    private void validarCamposObrigatorios(TipoServico servico) throws SQLException, NegocioException {
        buscarEspecialidade(servico.getEspecialidadeNecessaria());
    }

    public void validarNome(String nome) throws NegocioException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NegocioException("Erro: O nome do serviço é obrigatório.");
        }

        if (nome.trim().length() < 3) {
            throw new NegocioException("Erro: O nome do serviço deve ter ao menos 3 caracteres.");
        }

        if (nome.matches(".*\\d.*")) {
            throw new NegocioException("Erro: O nome do serviço não pode conter números.");
        }
    }

    public void validarDuracaoMinutos(int duracao) throws NegocioException {
        if (duracao < 5) {
            throw new NegocioException("Erro: A duração do serviço deve ser de no mínimo 5 minutos.");
        }
    }

    private Especialidade buscarEspecialidade(Especialidade especialidade) throws SQLException, NegocioException {
        if (especialidade == null || especialidade.getId() <= 0) {
            throw new NegocioException("Erro: O serviço deve ser vinculado a uma especialidade válida.");
        }

        Especialidade especialidadeExistente = this.especialidadeDAO.buscarPorId(especialidade.getId());

        if (especialidadeExistente == null) {
            throw new NegocioException(
                    "Erro: A especialidade com o ID [" + especialidade.getId()
                            + "] não foi encontrada. Cadastre a especialidade primeiro.");
        }
        return especialidadeExistente;
    }

    private void validarUnicidade(TipoServico servico) throws NegocioException, SQLException {
        TipoServico existente = dao.buscarPorNomeEEspecialidadeIgnoreCase(
                servico.getNome(),
                servico.getEspecialidadeNecessaria().getId());

        if (existente != null) {
            throw new NegocioException(
                    "Erro: Já existe um serviço com este nome para a especialidade.");
        }
    }
}
