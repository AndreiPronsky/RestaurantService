package org.pronsky.data.connection;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.pronsky.exceptions.ConnectionException;
import org.pronsky.utils.PropertyReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Log4j
@RequiredArgsConstructor
public class ConnectionUtil {
    private final PropertyReader propertyReader = PropertyReader.getInstance();
    private final String url = propertyReader.getUrl();
    private final String user = propertyReader.getUser();
    private final String password = propertyReader.getPassword();

    public Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectionException("Unable to connect to database");
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new ConnectionException("Unable to find postgres driver");
        }
    }
}
