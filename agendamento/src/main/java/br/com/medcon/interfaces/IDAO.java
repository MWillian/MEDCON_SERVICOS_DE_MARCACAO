package br.com.medcon.interfaces;
import java.sql.SQLException;
import java.util.List;

public interface IDAO<T> {
    void salvar(T objeto) throws SQLException;
    void atualizar(T objeto) throws SQLException;
    void deletar(int id) throws SQLException;
    T buscarPorId(int id) throws SQLException;
    List<T> listarTodos() throws SQLException;
}
