package br.com.medcon.dao;
import br.com.medcon.interfaces.IDAO;
import br.com.medcon.vo.Paciente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO implements IDAO<Paciente> {
    private final ConexaoFactory factory;
    public PacienteDAO() {
        this.factory = new ConexaoFactory();
    }

    @Override
    public void salvar(Paciente paciente) throws SQLException {
        String sqlPessoa = "INSERT INTO tb_pessoa (nome, cpf, data_nascimento, telefone, endereco) VALUES (?, ?, ?, ?, ?)";
        String sqlPaciente = "INSERT INTO tb_paciente (id_pessoa, cartao_sus) VALUES (?, ?)";
        Connection conn = null;

        try {
            conn = factory.getConexao();
            conn.setAutoCommit(false); 

            int idPessoaGerado = 0;
            try (PreparedStatement stmtPessoa = conn.prepareStatement(sqlPessoa, Statement.RETURN_GENERATED_KEYS)) {
                stmtPessoa.setString(1, paciente.getNome());
                stmtPessoa.setString(2, paciente.getCpf());
                stmtPessoa.setString(3, paciente.getDataNascimento().toString());
                stmtPessoa.setString(4, paciente.getTelefone());
                stmtPessoa.setString(5, paciente.getEndereco());
                stmtPessoa.executeUpdate();

                try (ResultSet result = stmtPessoa.getGeneratedKeys()) {
                    if (result.next()) {
                        idPessoaGerado = result.getInt(1);
                        paciente.setId(idPessoaGerado);
                    }
                }
            }

            try (PreparedStatement stmtPaciente = conn.prepareStatement(sqlPaciente)) {
                stmtPaciente.setInt(1, idPessoaGerado); 
                stmtPaciente.setString(2, paciente.getCartaoSus());
                stmtPaciente.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; 
        } finally {
           if (conn != null) factory.fecharConexao(conn);
        }
    }

    @Override
    public void atualizar(Paciente paciente) throws SQLException {
        String sqlPessoa = "UPDATE tb_pessoa SET nome=?, cpf=?, data_nascimento=?, telefone=?, endereco=? WHERE id=?";
        String sqlPaciente = "UPDATE tb_paciente SET cartao_sus=? WHERE id_pessoa=?";
        Connection conn = null;

        try {
            conn = factory.getConexao();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtPessoa = conn.prepareStatement(sqlPessoa)) {
                stmtPessoa.setString(1, paciente.getNome());
                stmtPessoa.setString(2, paciente.getCpf());
                stmtPessoa.setString(3, paciente.getDataNascimento().toString());
                stmtPessoa.setString(4, paciente.getTelefone());
                stmtPessoa.setString(5, paciente.getEndereco());
                stmtPessoa.setInt(6, paciente.getId());
                stmtPessoa.executeUpdate();
            }

            try (PreparedStatement stmtPac = conn.prepareStatement(sqlPaciente)) {
                stmtPac.setString(1, paciente.getCartaoSus());
                stmtPac.setInt(2, paciente.getId());
                stmtPac.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) factory.fecharConexao(conn);
        }
    }

    @Override
    public void deletar(int id) throws SQLException { 
        String sql = "DELETE FROM tb_pessoa WHERE id = ?";
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.execute();
        }
    }

    @Override
    public Paciente buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.*, pac.cartao_sus FROM tb_pessoa p " +
                     "JOIN tb_paciente pac ON p.id = pac.id_pessoa " +
                     "WHERE p.id = ?";
        Paciente paciente = null;
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    paciente = montarObjeto(result);
                }
            }
        }
        return paciente;
    }
    
    public Paciente buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT p.*, pac.cartao_sus FROM tb_pessoa p " +
                     "JOIN tb_paciente pac ON p.id = pac.id_pessoa " +
                     "WHERE p.cpf = ?";
        Paciente paciente = null;
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    paciente = montarObjeto(result);
                }
            }
        }
        return paciente;
    }

    @Override
    public List<Paciente> listarTodos() throws SQLException {
        String sql = "SELECT p.*, pac.cartao_sus FROM tb_pessoa p " +
                     "JOIN tb_paciente pac ON p.id = pac.id_pessoa";
        List<Paciente> lista = new ArrayList<>();
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet result = stmt.executeQuery()) {
            while (result.next()) {
                lista.add(montarObjeto(result));
            }
        }
        return lista;
    }

    private Paciente montarObjeto(ResultSet result) throws SQLException {
        Paciente p = new Paciente();
        p.setId(result.getInt("id")); 
        p.setNome(result.getString("nome"));
        p.setCpf(result.getString("cpf"));
        String dataTexto = result.getString("data_nascimento"); 
        if (dataTexto != null) {
            p.setDataNascimento(LocalDate.parse(dataTexto));
        }
        p.setTelefone(result.getString("telefone"));
        p.setEndereco(result.getString("endereco"));
        p.setCartaoSus(result.getString("cartao_sus"));
        return p;
    }
}