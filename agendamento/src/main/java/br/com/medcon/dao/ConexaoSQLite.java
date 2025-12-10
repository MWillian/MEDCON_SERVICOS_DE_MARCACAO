package br.com.medcon.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import br.com.medcon.interfaces.IConexao;

public class ConexaoSQLite implements IConexao {
    private static final String URL_CONEXAO = "jdbc:sqlite:C:\\Users\\Calebe\\Desktop\\Atividade_POO\\clinica.db";
    private static final String DRIVER = "org.sqlite.JDBC";
    @Override
    public Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver JDBC não encontrado.");
            throw new SQLException("Driver JDBC não encontrado: " + DRIVER);
        }
        return DriverManager.getConnection(URL_CONEXAO);
    }
    @Override
    @SuppressWarnings("CallToPrintStackTrace") //não sei porque ta dand o erro no printStackTrace
    public void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}