package br.com.medcon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.medcon.enums.CargoProfissional;
import br.com.medcon.interfaces.IDAO;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.ProfissionalSaude;

public class ProfissionalSaudeDAO implements IDAO<ProfissionalSaude> {
    private final ConexaoFactory factory;

    public ProfissionalSaudeDAO() {
        this.factory = new ConexaoFactory();
    }

    @Override
    public void salvar(ProfissionalSaude profissional) throws SQLException {
        String sqlPessoa = "INSERT INTO tb_pessoa (nome, cpf, data_nascimento, telefone, endereco) VALUES (?, ?, ?, ?, ?)";
        String sqlProfissional = "INSERT INTO tb_profissional (id_pessoa, registro_profissional, tipo_profissional, id_especialidade) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmtPessoa = null;
        PreparedStatement stmtProf = null;
        try {
            conn = factory.getConexao();
            conn.setAutoCommit(false);
            stmtPessoa = conn.prepareStatement(sqlPessoa, Statement.RETURN_GENERATED_KEYS);
            stmtPessoa.setString(1, profissional.getNome());
            stmtPessoa.setString(2, profissional.getCpf());
            stmtPessoa.setString(3,
                    profissional.getDataNascimento() != null ? profissional.getDataNascimento().toString() : null);
            stmtPessoa.setString(4, profissional.getTelefone());
            stmtPessoa.setString(5, profissional.getEndereco());
            stmtPessoa.executeUpdate();
            int idPessoaGerado = 0;
            try (ResultSet result = stmtPessoa.getGeneratedKeys()) {
                if (result.next()) {
                    idPessoaGerado = result.getInt(1);
                    profissional.setId(idPessoaGerado);
                }
            }
            stmtProf = conn.prepareStatement(sqlProfissional);
            stmtProf.setInt(1, idPessoaGerado);
            stmtProf.setString(2, profissional.getRegistroProfissional());
            stmtProf.setString(3, profissional.getTipo().name());
            stmtProf.setInt(4, profissional.getEspecialidade().getId());
            stmtProf.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (stmtPessoa != null)
                stmtPessoa.close();
            if (stmtProf != null)
                stmtProf.close();
            if (conn != null)
                factory.fecharConexao(conn);
        }
    }

    @Override
    public void atualizar(ProfissionalSaude profissional) throws SQLException {
        String sqlPessoa = "UPDATE tb_pessoa SET nome=?, cpf=?, data_nascimento=?, telefone=?, endereco=? WHERE id=?";
        String sqlProf = "UPDATE tb_profissional SET registro_profissional=?, tipo_profissional=?, id_especialidade=? WHERE id_pessoa=?";
        Connection conn = null;
        try {
            conn = factory.getConexao();
            conn.setAutoCommit(false);
            try (PreparedStatement stmtP = conn.prepareStatement(sqlPessoa)) {
                stmtP.setString(1, profissional.getNome());
                stmtP.setString(2, profissional.getCpf());
                stmtP.setString(3,
                        profissional.getDataNascimento() != null ? profissional.getDataNascimento().toString() : null);
                stmtP.setString(4, profissional.getTelefone());
                stmtP.setString(5, profissional.getEndereco());
                stmtP.setInt(6, profissional.getId());
                stmtP.executeUpdate();
            }
            try (PreparedStatement stmtPr = conn.prepareStatement(sqlProf)) {
                stmtPr.setString(1, profissional.getRegistroProfissional());
                stmtPr.setString(2, profissional.getTipo().name());
                stmtPr.setLong(3, profissional.getEspecialidade().getId());
                stmtPr.setInt(4, profissional.getId());
                stmtPr.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (conn != null)
                factory.fecharConexao(conn);
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
    public ProfissionalSaude buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.*, prof.registro_profissional, prof.tipo_profissional, "
                + "e.id as id_esp, e.nome as nome_esp, e.descricao as desc_esp "
                + "FROM tb_pessoa p "
                + "JOIN tb_profissional prof ON p.id = prof.id_pessoa "
                + "JOIN tb_especialidade e ON prof.id_especialidade = e.id "
                + "WHERE p.id = ?";
        ProfissionalSaude profissional = null;
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    profissional = montarObjeto(result);
                }
            }
        }
        return profissional;
    }

    public ProfissionalSaude buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT p.*, prof.registro_profissional, prof.tipo_profissional, "
                + "e.id as id_esp, e.nome as nome_esp, e.descricao as desc_esp "
                + "FROM tb_pessoa p "
                + "JOIN tb_profissional prof ON p.id = prof.id_pessoa "
                + "JOIN tb_especialidade e ON prof.id_especialidade = e.id "
                + "WHERE p.cpf = ?";
        ProfissionalSaude profissional = null;
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    profissional = montarObjeto(result);
                }
            }
        }
        return profissional;
    }

    public ProfissionalSaude buscarPorRegistroProfissional(String registro) throws SQLException {
        String sql = "SELECT p.*, prof.registro_profissional, prof.tipo_profissional, "
                + "e.id as id_esp, e.nome as nome_esp, e.descricao as desc_esp "
                + "FROM tb_pessoa p "
                + "JOIN tb_profissional prof ON p.id = prof.id_pessoa "
                + "JOIN tb_especialidade e ON prof.id_especialidade = e.id "
                + "WHERE prof.registro_profissional = ?";
        ProfissionalSaude profissional = null;
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, registro);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    profissional = montarObjeto(result);
                }
            }
        }
        return profissional;
    }

    @Override
    public List<ProfissionalSaude> listarTodos() throws SQLException {
        String sql = "SELECT p.*, prof.registro_profissional, prof.tipo_profissional, "
                + "e.id as id_esp, e.nome as nome_esp, e.descricao as desc_esp "
                + "FROM tb_pessoa p "
                + "JOIN tb_profissional prof ON p.id = prof.id_pessoa "
                + "JOIN tb_especialidade e ON prof.id_especialidade = e.id";
        List<ProfissionalSaude> lista = new ArrayList<>();
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet result = stmt.executeQuery()) {
            while (result.next()) {
                lista.add(montarObjeto(result));
            }
        }
        return lista;
    }

    private ProfissionalSaude montarObjeto(ResultSet result) throws SQLException {
        ProfissionalSaude p = new ProfissionalSaude();
        p.setId(result.getInt("id"));
        p.setNome(result.getString("nome"));
        p.setCpf(result.getString("cpf"));
        String dataTexto = result.getString("data_nascimento");
        if (dataTexto != null) {
            p.setDataNascimento(LocalDate.parse(dataTexto));
        }
        p.setTelefone(result.getString("telefone"));
        p.setEndereco(result.getString("endereco"));
        p.setRegistroProfissional(result.getString("registro_profissional"));
        p.setTipo(CargoProfissional.valueOf(result.getString("tipo_profissional")));
        Especialidade esp = new Especialidade();
        esp.setId(result.getInt("id_esp"));
        esp.setNome(result.getString("nome_esp"));
        esp.setDescricao(result.getString("desc_esp"));
        p.setEspecialidade(esp);
        return p;
    }

    public int buscarIdPessoaPorCpf(String cpf) throws SQLException {
        String sql = "SELECT id FROM tb_pessoa WHERE cpf = ?";
        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt("id");
            }
        }
        return -1;
    }

    public void salvarApenasVinculo(ProfissionalSaude profissional) throws SQLException {
        String sql = "INSERT INTO tb_profissional (id_pessoa, registro_profissional, tipo_profissional, id_especialidade) VALUES (?, ?, ?, ?)";

        try (Connection conn = factory.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, profissional.getId());
            stmt.setString(2, profissional.getRegistroProfissional());
            stmt.setString(3, profissional.getTipo().name());
            stmt.setInt(4, profissional.getEspecialidade().getId());

            stmt.execute();
        }
    }
}
