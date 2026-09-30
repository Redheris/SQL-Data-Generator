package dev.redheris.sqldatagenerator.db;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColumnDataExtractService {
    private final ConnectionService connectionService;

    public ColumnDataExtractService(ConnectionService connectionService) {
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
