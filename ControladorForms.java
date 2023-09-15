package atividade.example.formulariospark;

import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ControladorForms {
    public static void main(String[] args) {
        port(8080); 
        
        //conexão com o bd
		String driverName = "org.postgresql.Driver";                    
		String serverName = "localhost";
		String mydatabase = "lol";
		int porta = 5432;
		String url = "jdbc:postgresql://" + serverName + ":" + porta +"/" + mydatabase;
		String username = "postgres";
		String password = "a!ves2004";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS personagens (" +
                    "id SERIAL PRIMARY KEY," +
                    "nome VARCHAR(255) NOT NULL," +
                    "dataL VARCHAR(255) NOT NULL," +
                    "regiao VARCHAR(255) NOT NULL," +
                    "descricao TEXT NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Map<Integer, Personagem> personagens = new HashMap<>();
        
        get("/", (req, res) -> {
            return new ModelAndView(null, "formulario.html");
        }, new MustacheTemplateEngine());

        post("/processar", (req, res) -> {
            String nome = req.queryParams("nome");
            String dataL = req.queryParams("data");
            String regiao = req.queryParams("regiao");
            String descricao = req.queryParams("descricao");

            try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "INSERT INTO personagens (nome, dataL, regiao, descricao) VALUES (?, ?, ?, ?)",
                         Statement.RETURN_GENERATED_KEYS)) {

                preparedStatement.setString(1, nome);
                preparedStatement.setString(2, dataL);
                preparedStatement.setString(3, regiao);
                preparedStatement.setString(4, descricao);

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        Personagem personagem = new Personagem(id, nome, dataL, regiao, descricao);
                        personagens.put(personagem.getId(), personagem);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            res.redirect("/");

            return null;
        });
    }
}

