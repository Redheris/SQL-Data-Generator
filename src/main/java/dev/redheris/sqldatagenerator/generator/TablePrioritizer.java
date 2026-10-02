package dev.redheris.sqldatagenerator.generator;

import dev.redheris.sqldatagenerator.request.model.ColumnConfig;
import dev.redheris.sqldatagenerator.request.model.TableConfig;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TablePrioritizer {
    public List<TableConfig> prioritizeTables(TableConfig[] tableConfigs) {
        Map<String, TableConfig> tables = Arrays.stream(tableConfigs)
                .collect(Collectors.toMap(
                        TableConfig::name,
                        tableConfig -> tableConfig
                ));
        Set<String> requestedTables = tables.keySet();
        Set<String> prioritizedTables = new HashSet<>();
        List<TableConfig> prioritized = new ArrayList<>();

        for (TableConfig tableConfig : tableConfigs) {
            if (!prioritizedTables.contains(tableConfig.name())) {
                prioritizeTable(
                        tables, requestedTables, prioritizedTables, prioritized,
                        tableConfig, new LinkedList<>(List.of(tableConfig.name()))
                );
            }
        }

        return prioritized;
    }

    private void prioritizeTable(
            Map<String, TableConfig> tables,
            Set<String> requestedTables,
            Set<String> prioritizedTables,
            List<TableConfig> prioritized,
            TableConfig tableConfig,
            LinkedList<String> dependencyStack
    ) {
        var dependencies = getTableDependencies(tableConfig);
        if (!dependencies.isEmpty()) {
            for (String tableDep : dependencies) {
                if (!requestedTables.contains(tableDep)) {
                    continue;
                }
                if (prioritizedTables.contains(tableDep)) {
                    continue;
                }

                boolean cyclicDeps = dependencyStack.contains(tableDep);
                dependencyStack.add(tableDep);
                if (cyclicDeps) {
                    throw new IllegalStateException(
                            "Cyclic tables dependency: \"%s\". Dependency stack: [%s]"
                                    .formatted(
                                            tableDep,
                                            String.join(" -> ", dependencyStack)
                                    ));
                }

                prioritizeTable(
                        tables, requestedTables, prioritizedTables, prioritized,
                        tables.get(tableDep), dependencyStack
                );
            }
        }

        dependencyStack.pollLast();
        prioritizedTables.add(tableConfig.name());
        prioritized.add(tableConfig);
    }

    private List<String> getTableDependencies(TableConfig tableConfig) {
        List<String> deps = new ArrayList<>();

        for (ColumnConfig column : tableConfig.columns()) {
            if (column.foreignKey() != null) {
                deps.add(column.foreignKey().table());
            }
        }

        return deps;
    }
}
