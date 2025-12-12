package br.com.medcon.dao;

import br.com.medcon.enums.CargoProfissional;
import br.com.medcon.vo.Especialidade;
import br.com.medcon.vo.PostoSaude;
import br.com.medcon.vo.ProfissionalPosto;
import br.com.medcon.vo.ProfissionalSaude;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProfissionalPostoDAO {
    private final ConexaoFactory factory;

    public ProfissionalPostoDAO() {
        this.factory = new ConexaoFactory();
    }

    public void vincular(int idProfissional, int idPosto) throws SQLException {
        String sql = "INSERT INTO tb_profissional_posto (id_profissional, id_posto) VALUES (?, ?)";
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProfissional);
            stmt.setInt(2, idPosto);
            stmt.execute();
        }
    }

    public void desvincular(int idProfissional, int idPosto) throws SQLException {
        String sql = "DELETE FROM tb_profissional_posto WHERE id_profissional = ? AND id_posto = ?";
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProfissional);
            stmt.setInt(2, idPosto);
            stmt.execute();
        }
    }

    public boolean verificarVinculoExistente(int idProfissional, int idPosto) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_profissional_posto WHERE id_profissional = ? AND id_posto = ?";
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProfissional);
            stmt.setInt(2, idPosto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<PostoSaude> listarPostosDoProfissional(int idProfissional) throws SQLException {
        String sql = "SELECT p.* FROM tb_posto p " +
                     "JOIN tb_profissional_posto pp ON p.id = pp.id_posto " +
                     "WHERE pp.id_profissional = ?";
        List<PostoSaude> lista = new ArrayList<>();
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProfissional);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    PostoSaude p = new PostoSaude();
                    p.setId(result.getInt("id"));
                    p.setNome(result.getString("nome"));
                    p.setEndereco(result.getString("endereco"));
                    p.setTelefone(result.getString("telefone"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    public void salvar(ProfissionalPosto objeto) throws SQLException {
        vincular(objeto.getProfissional().getId(), objeto.getPosto().getId());
    }

    public List<ProfissionalPosto> listarTodos() throws SQLException {
        String sql = "SELECT pp.id_profissional, pp.id_posto, " +
                     "p_prof.nome as nome_prof, p_prof.cpf, prof.registro_profissional, prof.tipo_profissional, " +
                     "e.id as id_esp, e.nome as nome_esp, " +
                     "posto.id as pid, posto.nome as nome_posto, posto.endereco, posto.telefone " +
                     "FROM tb_profissional_posto pp " +
                     "JOIN tb_profissional prof ON pp.id_profissional = prof.id_pessoa " +
                     "JOIN tb_pessoa p_prof ON prof.id_pessoa = p_prof.id " +
                     "JOIN tb_especialidade e ON prof.id_especialidade = e.id " +
                     "JOIN tb_posto posto ON pp.id_posto = posto.id";
        List<ProfissionalPosto> lista = new ArrayList<>();
        try (Connection conn = factory.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet result = stmt.executeQuery()) {
            while (result.next()) {
                ProfissionalPosto pp = new ProfissionalPosto();

                ProfissionalSaude prof = new ProfissionalSaude();
                prof.setId(result.getInt("id_profissional"));
                prof.setNome(result.getString("nome_prof"));
                prof.setCpf(result.getString("cpf"));
                prof.setRegistroProfissional(result.getString("registro_profissional"));
                prof.setTipo(CargoProfissional.valueOf(result.getString("tipo_profissional")));

                Especialidade esp = new Especialidade();
                esp.setId(result.getInt("id_esp"));
                esp.setNome(result.getString("nome_esp"));
                prof.setEspecialidade(esp);

                PostoSaude posto = new PostoSaude();
                posto.setId(result.getInt("pid"));
                posto.setNome(result.getString("nome_posto"));
                posto.setEndereco(result.getString("endereco"));
                posto.setTelefone(result.getString("telefone"));

                pp.setProfissional(prof);
                pp.setPosto(posto);
                lista.add(pp);
            }
        }
        return lista;
    }
}
