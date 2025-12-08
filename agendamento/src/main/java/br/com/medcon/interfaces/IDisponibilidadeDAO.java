package br.com.medcon.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.vo.Disponibilidade;

public interface IDisponibilidadeDAO {
    void salvar(Disponibilidade disponibilidade)throws SQLException;

    void atualizar(Disponibilidade disponibilidade)throws SQLException;

    void deletar(int id) throws SQLException;

    Disponibilidade buscarPorId(int id) throws SQLException;

    List<Disponibilidade>buscarTodos() throws SQLException;

    List<Disponibilidade> buscarPorMedico(int idMedico) throws SQLException;

    List<Disponibilidade> buscarPorPosto(int idMedico) throws SQLException;

    List<Disponibilidade> buscarPorEspecialidade(int idEspecialidade) throws SQLException;
}
