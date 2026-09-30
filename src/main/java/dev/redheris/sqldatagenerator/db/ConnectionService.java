package dev.redheris.sqldatagenerator.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.redheris.sqldatagenerator.request.model.DBAuthData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConnectionService implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(ConnectionService.class);
    private HikariDataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public void connect(DBAuthData dbAuthData) {
        disconnect();

        HikariConfig connectionConfig = new HikariConfig();
        connectionConfig.setJdbcUrl(dbAuthData.url());
        connectionConfig.setUsername(dbAuthData.username());
        connectionConfig.setPassword(dbAuthData.password());

        dataSource = new HikariDataSource(connectionConfig);
        jdbcTemplate = new JdbcTemplate(dataSource);

        log.info("Connected to the database \"{}\" as {}", dataSource.getJdbcUrl(), dataSource.getUsername());
    }

    public JdbcTemplate getJdbcTemplate() {
        if (dataSource == null) {
            throw new IllegalStateException("Database connection is not established");
        }
        return jdbcTemplate;
    }

    public void disconnect() {
        if (dataSource != null) {
            String url = dataSource.getJdbcUrl();
            dataSource.close();
            dataSource = null;
            jdbcTemplate = null;
            log.info("Disconnected from database \"{}\"", url);
        }
    }

    @Override
    public void close() {
        disconnect();
    }
}
