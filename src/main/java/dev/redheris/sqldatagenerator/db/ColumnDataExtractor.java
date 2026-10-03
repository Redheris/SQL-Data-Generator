package dev.redheris.sqldatagenerator.db;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ColumnDataExtractor {
    private final ConnectionService connectionService;

    public ColumnDataExtractor(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public List<Object> selectColumn(String table, String column) {
        var jdbcTemplate = connectionService.getJdbcTemplate();

        String sql = "SELECT %s FROM %s".formatted(column, table);

        return jdbcTemplate.queryForList(sql)
                .stream()
                .flatMap(m -> m.values().stream())
                .toList();
    }
}
