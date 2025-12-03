package br.com.medcon.interfaces;
import java.sql.Connection;
import java.sql.SQLException;

public interface IConexao {
    Connection getConnection() throws SQLException;
    void closeConnection(Connection conn);
}