package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class DatabaseManager {
    // O arquivo .db ficará na raiz do projeto (fora da pasta backend)
    private static final String URL = "jdbc:sqlite:usuarios.db";

    public static void inicializarBanco() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                             "nome TEXT NOT NULL," +
                             "senha TEXT NOT NULL);";
                stmt.execute(sql);
                System.out.println("[DB] Banco de dados pronto para uso.");
            }
        } catch (Exception e) {
            System.out.println("[DB] Erro ao inicializar banco: " + e.getMessage());
        }
    }

    public static void cadastrarUsuario(String nome, String senha) {
        String sql = "INSERT INTO usuarios(nome, senha) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, senha);
            pstmt.executeUpdate();
            System.out.println("[DB] Usuário " + nome + " cadastrado!");
        } catch (Exception e) {
            System.out.println("[DB] Erro ao cadastrar: " + e.getMessage());
        }
    }
}