package ua.edu.ucu.apps;

import java.sql.*;

public class CachedDocument implements Document {
    private Document document;
    private String identifier;
    private String DB_URL = "jdbc:sqlite:/Users/my.bd";

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
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            String query = "INSERT INTO table (identifier, result) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, identifier);
            preparedStatement.setString(2, result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String fromTable() {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            String query = "SELECT result FROM table WHERE identifier = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
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
