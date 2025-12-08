package br.com.medcon.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.medcon.vo.Disponibilidade;
import br.com.medcon.vo.PostoSaude;
import br.com.medcon.vo.ProfissionalSaude;

public interface IDisponibilidadeDAO {
    void salvar(Disponibilidade disponibilidade)throws SQLException;

    void atualizar(Disponibilidade disponibilidade)throws SQLException;

    void deletar(int id) throws SQLException;

    Disponibilidade buscarPorId(int id) throws SQLException;

    List<Disponibilidade>buscarTodos() throws SQLException;

    ProfissionalSaude buscaPorMedico(int idMedico) throws SQLException;

    PostoSaude buscaPorPosto(int idMedico) throws SQLException;
}
