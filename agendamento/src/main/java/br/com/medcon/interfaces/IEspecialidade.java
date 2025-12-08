package br.com.medcon.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.vo.Especialidade;

public interface IEspecialidade {
    void salvar(Especialidade especialidade) throws SQLException;
    void atualizar(Especialidade especialidade) throws SQLException;
    void deletar(int id) throws SQLException;
    Especialidade buscarPorId(int id) throws SQLException;
    List<Especialidade> listarTodos() throws SQLException;
    Especialidade buscarPorNome(String nome) throws SQLException;
}

