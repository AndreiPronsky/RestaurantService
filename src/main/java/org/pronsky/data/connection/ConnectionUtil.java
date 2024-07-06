package org.pronsky.data.connection;

import lombok.RequiredArgsConstructor;
import org.pronsky.exceptions.ConnectionException;
import org.pronsky.utils.PropertyReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@RequiredArgsConstructor
public class ConnectionUtil {
    private final PropertyReader propertyReader;
    private final String url = propertyReader.getUrl();
    private final String user = propertyReader.getUser();
    private final String password = propertyReader.getPassword();

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new ConnectionException("Unable to connect to database", e);
        }
    }
}
