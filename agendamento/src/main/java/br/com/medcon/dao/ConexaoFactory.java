package br.com.medcon.dao;
import br.com.medcon.interfaces.IConexao;
import java.sql.Connection;
import java.sql.SQLException;

public class ConexaoFactory {
    private final IConexao conexaoInstance;
    public ConexaoFactory() {
        this.conexaoInstance = new ConexaoSQLite();
    }
    public Connection getConexao() throws SQLException {
        return this.conexaoInstance.getConnection();
    }
    public void fecharConexao(Connection conn) {
        this.conexaoInstance.closeConnection(conn);
    }
}