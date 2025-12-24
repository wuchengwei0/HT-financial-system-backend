package com.example.scaffold.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class HutoolH2ExportUtil {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 导出所有表为 xlsx
     */
    public byte[] exportXlsxAllTables() throws SQLException, IOException {
        try (Connection conn = dataSource.getConnection();
             cn.hutool.poi.excel.ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(true); // true: xlsx格式
             java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            DatabaseMetaData metaData = conn.getMetaData();
            List<String> userTableNames = new ArrayList<>();
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    userTableNames.add(tableName);
                }
            }
            if (userTableNames.isEmpty()) {
                // 没有表时生成默认sheet
                writer.writeRow(cn.hutool.core.collection.CollUtil.newArrayList("No tables found in H2 database!"));
            } else {
                for (String tableName : userTableNames) {
                    List<Map<String, Object>> rowList = new ArrayList<>();
                    boolean querySuccess = false;
                    try (Statement st = conn.createStatement();
                         ResultSet rs = st.executeQuery("SELECT * FROM \"" + tableName + "\"")) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();
                        while (rs.next()) {
                            Map<String, Object> row = new LinkedHashMap<>();
                            for (int i = 1; i <= columnCount; i++) {
                                row.put(rsmd.getColumnLabel(i), rs.getObject(i));
                            }
                            rowList.add(row);
                        }
                        querySuccess = true;
                    } catch (Exception e) {
                        // 跳过不存在的表、视图或者系统表等
                        continue;
                    }
                    if (querySuccess && cn.hutool.core.collection.CollUtil.isNotEmpty(rowList)) {
                        writer.setSheet(tableName);
                        writer.write(rowList, true);
                    } else {
                        // 仅写表头
                        try (Statement st = conn.createStatement();
                             ResultSet rs = st.executeQuery("SELECT * FROM \"" + tableName + "\" WHERE 1=0")) {
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int columnCount = rsmd.getColumnCount();
                            Map<String, Object> head = new LinkedHashMap<>();
                            for (int i = 1; i <= columnCount; i++) {
                                head.put(rsmd.getColumnLabel(i), null);
                            }
                            writer.setSheet(tableName);
                            writer.write(cn.hutool.core.collection.CollUtil.newArrayList(head), true);
                        }
                    }
                }
                // 移除 Hutool/POI 默认生成的 sheet1（仅保留你的业务 sheet）
                int sheetCount = writer.getWorkbook().getNumberOfSheets();
                List<String> keepSheetNames = userTableNames;
                for (int i = sheetCount - 1; i >= 0; i--) {
                    String name = writer.getWorkbook().getSheetName(i);
                    if (!keepSheetNames.contains(name)) {
                        writer.getWorkbook().removeSheetAt(i);
                    }
                }
            }
            writer.flush(out, true);
            return out.toByteArray();
        }
    }

    /**
     * 导入Excel数据到H2
     * 自动按sheet名匹配表名，自动插入数据（忽略表不存在情况）
     */
    public void importXlsxToH2(byte[] xlsxBytes) throws IOException, SQLException {
        try (Connection conn = dataSource.getConnection();
             ByteArrayInputStream in = new ByteArrayInputStream(xlsxBytes)) {

            DatabaseMetaData metaData = conn.getMetaData();
            // 获取所有实际表名
            Set<String> tableNames = new HashSet<>();
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME").toLowerCase();
                    tableNames.add(tableName);
                }
            }

            ExcelReader reader = ExcelUtil.getReader(in);
            List<String> sheets = reader.getSheetNames();

            for (String sheetName : sheets) {
                // 不区分大小写匹配表名
                if (!tableNames.contains(sheetName.toLowerCase())) {
                    continue; // 跳过不存在的表
                }

                reader.setSheet(sheetName);
                List<Map<String, Object>> rows = reader.readAll();

                if (CollUtil.isNotEmpty(rows)) {
                    // 获取表的列信息，包括数据类型
                    Map<String, Integer> columnTypes = getTableColumnTypes(conn, sheetName);

                    List<String> columns = new ArrayList<>(rows.get(0).keySet());

                    // 构建SQL
                    StringBuilder sql = new StringBuilder();
                    sql.append("INSERT INTO \"").append(sheetName).append("\" (");
                    sql.append(String.join(",", columns));
                    sql.append(") VALUES (");
                    sql.append(String.join(",", Collections.nCopies(columns.size(), "?")));
                    sql.append(")");

                    // 逐行插入，单行失败跳过
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> row = rows.get(i);
                        try {
                            List<Object> values = new ArrayList<>();
                            for (String col : columns) {
                                Object value = row.get(col);
                                // 处理空值
                                if (value == null || "".equals(value)) {
                                    values.add(null);
                                } else {
                                    // 根据列数据类型转换
                                    values.add(convertValueByType(value, col, columnTypes));
                                }
                            }
                            // 单行插入
                            jdbcTemplate.update(sql.toString(), values.toArray());
                        } catch (Exception e) {
                            // 当前行插入失败，跳过此行继续下一行
                            System.out.println("跳过表 " + sheetName + " 第 " + (i+1) + " 行: " + e.getMessage());
                            continue; // 关键：跳过失败行，继续循环
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }
    
    // 获取表的列类型信息
    private Map<String, Integer> getTableColumnTypes(Connection conn, String tableName) throws SQLException {
        Map<String, Integer> columnTypes = new HashMap<>();
        DatabaseMetaData metaData = conn.getMetaData();

        try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                int dataType = columns.getInt("DATA_TYPE");
                columnTypes.put(columnName.toLowerCase(), dataType);
            }
        }
        return columnTypes;
    }

    // 根据列类型转换值
    private Object convertValueByType(Object value, String columnName, Map<String, Integer> columnTypes) {
        if (value == null) return null;

        String lowerColName = columnName.toLowerCase();
        Integer sqlType = columnTypes.get(lowerColName);

        if (sqlType == null) {
            // 如果没有找到列类型信息，直接返回原始值
            return value;
        }

        try {
            switch (sqlType) {
                case Types.INTEGER:
                case Types.SMALLINT:
                case Types.TINYINT:
                    if (value instanceof Number) {
                        return ((Number) value).intValue();
                    } else {
                        return Integer.parseInt(value.toString().trim());
                    }

                case Types.BIGINT:
                    if (value instanceof Number) {
                        return ((Number) value).longValue();
                    } else {
                        return Long.parseLong(value.toString().trim());
                    }

                case Types.DECIMAL:
                case Types.NUMERIC:
                case Types.DOUBLE:
                case Types.FLOAT:
                    if (value instanceof Number) {
                        return ((Number) value).doubleValue();
                    } else {
                        String str = value.toString().trim();
                        if (str.isEmpty()) return null;
                        return Double.parseDouble(str);
                    }

                case Types.BOOLEAN:
                case Types.BIT:
                    if (value instanceof Boolean) {
                        return value;
                    } else if (value instanceof Number) {
                        return ((Number) value).intValue() != 0;
                    } else {
                        String str = value.toString().trim().toLowerCase();
                        return "true".equals(str) || "1".equals(str) || "是".equals(str);
                    }

                case Types.DATE:
                case Types.TIMESTAMP:
                case Types.TIME:
                    if (value instanceof java.util.Date) {
                        return value;
                    } else {
                        // 尝试解析日期字符串
                        String str = value.toString().trim();
                        try {
                            return java.sql.Timestamp.valueOf(str);
                        } catch (Exception e) {
                            // 如果失败，返回原始字符串让数据库驱动处理
                            return str;
                        }
                    }

                default:
                    // 字符串类型或其他类型
                    return value.toString().trim();
            }
        } catch (Exception e) {
//            log.warn("转换列 {} 的值 {} 时出错: {}", columnName, value, e.getMessage());
            // 转换失败时返回原始值，让数据库驱动尝试处理
            return value;
        }
    }

    /**
     * 导出所有表为 SQL 文件（一行一个 INSERT 语句）
     */
//    public String exportSqlAllTables() throws SQLException {
//        StringBuilder sqlContent = new StringBuilder();
//        try (Connection conn = dataSource.getConnection()) {
//            DatabaseMetaData metaData = conn.getMetaData();
//            List<String> userTableNames = new ArrayList<>();
//            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
//                while (tables.next()) {
//                    String tableName = tables.getString("TABLE_NAME");
//                    userTableNames.add(tableName);
//                }
//            }
//            
//            for (String tableName : userTableNames) {
//                try (Statement st = conn.createStatement();
//                     ResultSet rs = st.executeQuery("SELECT * FROM \"" + tableName + "\"")) {
//                    ResultSetMetaData rsmd = rs.getMetaData();
//                    int columnCount = rsmd.getColumnCount();
//                    
//                    // 获取列名
//                    List<String> columnNames = new ArrayList<>();
//                    for (int i = 1; i <= columnCount; i++) {
//                        columnNames.add(rsmd.getColumnLabel(i));
//                    }
//                    
//                    // 生成 INSERT 语句
//                    while (rs.next()) {
//                        StringBuilder insertSql = new StringBuilder("INSERT INTO \"");
//                        insertSql.append(tableName).append("\" (");
//                        insertSql.append(String.join(", ", columnNames));
//                        insertSql.append(") VALUES (");
//                        
//                        List<String> values = new ArrayList<>();
//                        for (int i = 1; i <= columnCount; i++) {
//                            Object value = rs.getObject(i);
//                            if (value == null) {
//                                values.add("NULL");
//                            } else {
//                                // 根据类型格式化值
//                                String formattedValue = formatSqlValue(value, rsmd.getColumnType(i));
//                                values.add(formattedValue);
//                            }
//                        }
//                        insertSql.append(String.join(", ", values));
//                        insertSql.append(");");
//                        sqlContent.append(insertSql.toString()).append("\n");
//                    }
//                } catch (Exception e) {
//                    // 跳过不存在的表或查询失败的表
//                    continue;
//                }
//            }
//        }
//        return sqlContent.toString();
//    }
    /**
     * 导出所有表为 SQL 文件（一行一个 INSERT 语句）
     */
    public String exportSqlAllTables() throws SQLException {
        StringBuilder sqlContent = new StringBuilder();
        Set<String> reservedKeywords = getH2ReservedKeywords();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            List<String> userTableNames = new ArrayList<>();
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    userTableNames.add(tableName);
                }
            }

            for (String tableName : userTableNames) {
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT * FROM \"" + tableName + "\"")) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnCount = rsmd.getColumnCount();

                    // 获取列名，并对关键字添加反引号
                    List<String> columnNames = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = rsmd.getColumnLabel(i);
                        // 如果是H2保留关键字，添加反引号
                        if (isReservedKeyword(columnName, reservedKeywords)) {
                            columnNames.add("\"" + columnName + "\"");
                        } else {
                            columnNames.add(columnName);
                        }
                    }

                    // 生成 INSERT 语句
                    while (rs.next()) {
                        StringBuilder insertSql = new StringBuilder("INSERT INTO ");
                        // 检查表名是否是关键字
                        if (isReservedKeyword(tableName, reservedKeywords)) {
                            insertSql.append("\"").append(tableName).append("\"");
                        } else {
                            insertSql.append(tableName);
                        }
                        insertSql.append(" (");
                        insertSql.append(String.join(", ", columnNames));
                        insertSql.append(") VALUES (");

                        List<String> values = new ArrayList<>();
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rs.getObject(i);
                            if (value == null) {
                                values.add("NULL");
                            } else {
                                // 根据类型格式化值
                                String formattedValue = formatSqlValue(value, rsmd.getColumnType(i));
                                values.add(formattedValue);
                            }
                        }
                        insertSql.append(String.join(", ", values));
                        insertSql.append(");");
                        sqlContent.append(insertSql.toString()).append("\n");
                    }
                } catch (Exception e) {
                    // 跳过不存在的表或查询失败的表
                    continue;
                }
            }
        }
        return sqlContent.toString();
    }
    /**
     * 检查是否是H2保留关键字
     */
    private boolean isReservedKeyword(String word, Set<String> reservedKeywords) {
        if (word == null) return false;
        return reservedKeywords.contains(word.toUpperCase());
    }

    /**
     * 获取数据库的保留关键字列表
     * 注意：这里列出的是常见的关键字，可以根据需要扩展
     */
    private Set<String> getH2ReservedKeywords() {
        // H2常见保留关键字
        return new HashSet<>(Arrays.asList(
                "VALUE", "MONTH", "DATE", "USER", "ORDER", "GROUP", "SELECT",
                "INSERT", "UPDATE", "DELETE", "KEY", "LEVEL", "TIMESTAMP", "TIME",
                "YEAR", "DAY", "HOUR", "MINUTE", "SECOND", "TABLE", "COLUMN",
                "INDEX", "VIEW", "SCHEMA", "DATABASE", "CASCADE", "CHECK",
                "CONSTRAINT", "FOREIGN", "PRIMARY", "REFERENCES", "UNIQUE",
                "ALL", "AND", "ANY", "AS", "BETWEEN", "BY", "CASE", "CAST",
                "COLLATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
                "CURRENT_USER", "DISTINCT", "EXCEPT", "EXISTS", "FALSE", "FOR",
                "FROM", "FULL", "HAVING", "IN", "INNER", "INTERSECT", "INTO",
                "IS", "JOIN", "LEFT", "LIKE", "LIMIT", "NATURAL", "NOT", "NULL",
                "ON", "OR", "OUTER", "RIGHT", "ROW", "ROWNUM", "SET", "SOME",
                "SYSDATE", "SYSTIME", "SYSTIMESTAMP", "TODAY", "TOP", "TRUE",
                "UNION", "WHERE", "WITH", "WHEN", "THEN", "ELSE", "END"
        ));
    }

    /**
     * 格式化 SQL 值
     */
    private String formatSqlValue(Object value, int sqlType) {
        if (value == null) {
            return "NULL";
        }
        
        switch (sqlType) {
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.LONGVARCHAR:
            case Types.CLOB:
                // 转义单引号
                String str = value.toString().replace("'", "''");
                return "'" + str + "'";
                
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.NUMERIC:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.REAL:
                return value.toString();
                
            case Types.BOOLEAN:
            case Types.BIT:
                return value instanceof Boolean ? 
                    ((Boolean) value ? "1" : "0") : value.toString();
                
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                if (value instanceof java.sql.Date) {
                    return "'" + value.toString() + "'";
                } else if (value instanceof java.sql.Time) {
                    return "'" + value.toString() + "'";
                } else if (value instanceof java.sql.Timestamp) {
                    return "'" + value.toString() + "'";
                } else {
                    return "'" + value.toString() + "'";
                }
                
            default:
                // 默认作为字符串处理
                String defaultStr = value.toString().replace("'", "''");
                return "'" + defaultStr + "'";
        }
    }

    /**
     * 导入 SQL 文件到 H2（逐行执行，错误不影响后续行）
     */
    public void importSqlToH2(byte[] sqlBytes) throws SQLException {
        String sqlContent = new String(sqlBytes, java.nio.charset.StandardCharsets.UTF_8);
        // 按分号分割SQL语句（支持跨行的SQL语句）
        String[] statements = sqlContent.split(";");
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (int i = 0; i < statements.length; i++) {
                String statement = statements[i].trim();
                
                // 跳过空语句和注释
                if (statement.isEmpty() || statement.startsWith("--") || statement.startsWith("//")) {
                    continue;
                }
                
                // 移除多行注释
                statement = statement.replaceAll("/\\*[\\s\\S]*?\\*/", "").trim();
                if (statement.isEmpty()) {
                    continue;
                }
                
                try {
                    stmt.execute(statement);
                } catch (SQLException e) {
                    // 单条SQL执行失败，打印错误但继续执行下一条
                    System.out.println("SQL 导入第 " + (i + 1) + " 条语句失败: " + e.getMessage());
                    System.out.println("失败的 SQL: " + (statement.length() > 200 ? statement.substring(0, 200) + "..." : statement));
                    // 继续执行下一条，不中断
                    continue;
                }
            }
        }
    }

    /**
     * 清空所有表数据（不删除表结构）
     */
    public void truncateAllTables() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            List<String> userTableNames = new ArrayList<>();
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    userTableNames.add(tableName);
                }
            }
            
            try (Statement stmt = conn.createStatement()) {
                // 禁用外键约束（数据库）
                stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
                
                for (String tableName : userTableNames) {
                    try {
                        stmt.execute("TRUNCATE TABLE \"" + tableName + "\"");
                    } catch (Exception e) {
                        // 某些表可能无法清空，跳过
                        System.out.println("清空表 " + tableName + " 失败: " + e.getMessage());
                        continue;
                    }
                }
                
                // 重新启用外键约束
                stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
            }
        }
    }

    /**
     * Spring Boot启动时自动导入 resources/init.xlsx 或 init.sql 文件，如存在
     */
    @Autowired
    private com.example.scaffold.config.ImportXlsxProperties importXlsxProperties;

    @PostConstruct
    public void autoImportOnStartup() {
        String filePath = importXlsxProperties.getPath();
        if (filePath == null || filePath.trim().isEmpty()  ) {
            return; // 未配置则不导入
        }else if (!isValidExcelFile(filePath)) {
//            log.error("Excel文件无效: {}", filePath);
            return;
        }
        
        try {
            java.io.InputStream in;
            if (filePath.startsWith("classpath:")) {
                String cp = filePath.substring("classpath:".length());
                in = getClass().getClassLoader().getResourceAsStream(cp.replaceFirst("^/+", ""));
            } else if (filePath.startsWith("file:")) {
                in = new java.io.FileInputStream(filePath.substring(5));
            } else {
                in = new java.io.FileInputStream(filePath);
            }
            if (in != null) {
                byte[] fileBytes = in.readAllBytes();
                in.close();
                
                // 根据文件扩展名判断类型
                String lowerPath = filePath.toLowerCase();
                if (lowerPath.endsWith(".sql")) {
                    // 导入 SQL 文件
                    importSqlToH2(fileBytes);
                    System.out.println("启动时自动导入 SQL 文件成功: " + filePath);
                } else if (lowerPath.endsWith(".xlsx") || lowerPath.endsWith(".xls")) {
                    // 导入 XLSX 文件
                    importXlsxToH2(fileBytes);
                    System.out.println("启动时自动导入 XLSX 文件成功: " + filePath);
                } else {
                    System.out.println("不支持的文件类型: " + filePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("启动自动导入异常: " + e.getMessage());
        }
    }

    /**
     * 检查文件是否存在且有效
     * @param filePath 文件路径，支持 classpath:、file: 前缀或绝对/相对路径
     * @return true: 文件存在、可读且大小大于0KB；false: 文件不存在或有其他问题
     */
    public boolean isValidExcelFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
//            log.warn("文件路径为空");
            return false;
        }

        try {
            Resource resource;

            // 处理 classpath: 前缀
            if (filePath.startsWith("classpath:")) {
                String classPath = filePath.substring("classpath:".length());
                resource = new ClassPathResource(classPath);
            }
            // 处理 file: 前缀
            else if (filePath.startsWith("file:")) {
                resource = new FileSystemResource(filePath);
            }
            // 默认当作绝对路径或相对路径
            else {
                resource = new FileSystemResource(filePath);
            }

            // 1. 检查资源是否存在
            if (!resource.exists()) {
//                log.warn("文件不存在: {}", filePath);
                return false;
            }

            // 2. 检查是否是文件（而不是目录）
            if (!resource.isFile()) {
//                log.warn("指定路径不是文件: {}", filePath);
                return false;
            }

            // 3. 检查文件是否可读
            if (!resource.isReadable()) {
//                log.warn("文件不可读，请检查文件权限: {}", filePath);
                return false;
            }

            // 4. 检查文件大小
            try {
                // 尝试获取文件对象检查大小
                File file = resource.getFile();
                if (file.length() == 0) {
//                    log.warn("文件大小为0字节: {}", filePath);
                    return false;
                }
            } catch (FileNotFoundException e) {
                // 文件在JAR包内，getFile()会失败，通过InputStream检查
                try (InputStream is = resource.getInputStream()) {
                    // 尝试读取第一个字节来确认文件非空
                    if (is.read() == -1) {
//                        log.warn("文件在JAR包内且大小为0字节: {}", filePath);
                        return false;
                    }
                }
            }

            // 5. 可选：检查文件扩展名（如果需要严格限制Excel文件）
            if (!isExcelFileExtension(filePath)) {
//                log.warn("文件格式不正确，只支持Excel文件(.xlsx/.xls): {}", filePath);
                return false;
            }

//            log.debug("文件验证通过: {}", filePath);
            return true;

        } catch (IOException e) {
//            log.error("检查文件时发生IO异常: {}", filePath, e);
            return false;
        } catch (Exception e) {
//            log.error("检查文件时发生未知异常: {}", filePath, e);
            return false;
        }
    }

    /**
     * 检查文件扩展名是否为Excel格式
     * @param filePath 文件路径
     * @return true: 是Excel文件扩展名
     */
    private boolean isExcelFileExtension(String filePath) {
        if (filePath == null) {
            return false;
        }

        String lowerPath = filePath.toLowerCase();
        return lowerPath.endsWith(".xlsx") || lowerPath.endsWith(".xls");
    }
}
