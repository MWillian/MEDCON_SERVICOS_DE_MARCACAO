package br.com.medcon.bo;
import br.com.medcon.dao.TipoServicoDAO;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.TipoServico;
import java.sql.SQLException;
import java.util.List;
import br.com.medcon.bo.exception.NegocioException;
import br.com.medcon.dao.EspecialidadeDAO; 

public class TipoServicoBO {
    private final TipoServicoDAO dao;
    private final EspecialidadeDAO especialidadeDAO; 
        public TipoServicoBO(TipoServicoDAO dao, EspecialidadeDAO especialidadeDAO){
            this.dao = dao;
            this.especialidadeDAO = especialidadeDAO;
        }

        //MÉTODOS PRINCIPAIS
        public void salvar(TipoServico servico) throws NegocioException, SQLException {
            ValidarCamposObrigatorios(servico);
            dao.salvar(servico);
        }

        public List<TipoServico> listarTodos() throws SQLException {
            return dao.listarTodos();
        }

        public TipoServico buscarPorId(int id) throws SQLException, NegocioException {
            if (id <= 0) {
                throw new NegocioException("Tipo do Serviço deve ter um ID válido.");
            }
            return dao.buscarPorId(id);
        }

        //MÉTODOS AUXILIARES
        private void ValidarCamposObrigatorios(TipoServico servico) throws SQLException, NegocioException {
            ValidarNome(servico.getNome());
            ValidarDuracaoMinutos(servico.getDuracaoMinutos());
            BuscarEspecialidade(servico.getEspecialidadeNecessaria());
        }

        private void ValidarNome(String nomePaciente) throws NegocioException{
            if (nomePaciente == null || nomePaciente.trim().length() < 3) {
                throw new NegocioException("O nome do serviço é obrigatório e deve ter ao menos 3 caracteres.");
            }
        }

        private void ValidarDuracaoMinutos(int duracao) throws NegocioException{
            if (duracao < 5) {
                throw new NegocioException("A duração do serviço deve ser de no mínimo 5 minutos.");
            }
        }

        private Especialidade BuscarEspecialidade(Especialidade especialidade) throws SQLException, NegocioException{
            try {
                if (especialidade == null || especialidade.getId() <= 0) {
                    throw new NegocioException("O serviço deve ser vinculado a uma Especialidade válida.");
                }
                Especialidade especialidadeExistente = this.especialidadeDAO.buscarPorId(especialidade.getId());

                if (especialidadeExistente == null) {
                    throw new NegocioException("A Especialidade com o ID [" + especialidade.getId() + "] não foi encontrada no catálogo. Cadastre a especialidade primeiro.");
                }
                return especialidadeExistente;    
            } catch (SQLException e) {
                throw new SQLException("Erro ao buscar a especialidade no sistema");
            } 
        }
}
