package br.com.medcon.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.TipoServico;

public interface ITipoServicoDAO {

    void salvar(TipoServico tipoServico, Especialidade id_especialidade) throws SQLException;

    void atualizar(TipoServico tipoServico, Especialidade id_especialidade) throws SQLException;

    void deletar(int id) throws SQLException;

    TipoServico buscarPorId(int id) throws SQLException;

    List<TipoServico> listarTodos() throws SQLException;
    
}
