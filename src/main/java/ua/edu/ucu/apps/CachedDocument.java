package ua.edu.ucu.apps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CachedDocument implements Document {
    private Document document;
    private String identifier;
    private String dbURL = "jdbc:sqlite:/Users/my.bd";

    public CachedDocument(String path, String documentIdentifier) {
        this.document = new SmartDocument(path);
        this.identifier = documentIdentifier;
    }

    @Override
    public String parse() {
        String tableRes = fromTable();

        if (tableRes != null) {
            return tableRes;
        }

        String result = document.parse();
        toTable(result);
        return result;
    }

    private void toTable(String result) {
        try (Connection CONNECTION = DriverManager.getConnection(dbURL);
             Statement STATEMENT = CONNECTION.createStatement()) {
            String query = "INSERT INTO table (identifier, result) VALUES (?, ?)";
            PreparedStatement preparedStatement = CONNECTION.prepareStatement(query);
            preparedStatement.setString(1, identifier);
            preparedStatement.setString(2, result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String fromTable() {
        try (Connection CONNECTION = DriverManager.getConnection(dbURL);
             Statement STATEMENT = CONNECTION.createStatement()) {
            String query = "SELECT result FROM table WHERE identifier = ?";
            PreparedStatement preparedStatement = CONNECTION.prepareStatement(query);
            preparedStatement.setString(1, identifier);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("result");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
