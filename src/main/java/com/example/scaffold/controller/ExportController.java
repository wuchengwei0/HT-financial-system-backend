package com.example.scaffold.controller;

import com.example.scaffold.common.HutoolH2ExportUtil;
import com.example.scaffold.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/h2")
public class ExportController {
    @Autowired
    private HutoolH2ExportUtil exportUtil;

    @Operation(
            summary = "导出数据库为Excel文件",
            description = "将数据库中的所有表导出为Excel文件(.xlsx格式)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "导出成功，返回Excel文件流",
                    content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "导出失败",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            )
    })
    @GetMapping("/export/xlsx")
    public void exportH2Xlsx(HttpServletResponse response) throws IOException {
        try {
            byte[] data = exportUtil.exportXlsxAllTables();
            String fileName = URLEncoder.encode("h2-all-tables.xlsx", StandardCharsets.UTF_8.name());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.getOutputStream().write(data);
            response.getOutputStream().flush();
        } catch (Exception ex) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(Result.fail("导出失败: " + ex.getMessage()).toString());
        }
    }

    /** 导出所有表为 SQL 文件 **/
    @GetMapping("/export/sql")
    public void exportH2Sql(HttpServletResponse response) throws IOException {
        try {
            String sqlContent = exportUtil.exportSqlAllTables();
            String fileName = URLEncoder.encode("h2-all-tables.sql", StandardCharsets.UTF_8.name());
            response.setContentType("application/sql;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.getOutputStream().write(sqlContent.getBytes(StandardCharsets.UTF_8));
            response.getOutputStream().flush();
        } catch (Exception ex) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(Result.fail("导出失败: " + ex.getMessage()).toString());
        }
    }

    /** 导入文件（SQL 或 XLSX），先清空所有表数据，再导入 **/
    @Operation(
            summary = "导入文件到数据库",
            description = "支持导入SQL文件(.sql)或Excel文件(.xlsx/.xls)。导入前会先清空所有表数据，然后导入新数据。"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "导入成功",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "导入失败",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            )
    })
    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public Result<String> importFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件不能为空");
        }
        
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.fail("文件名不能为空");
            }
            
            String lowerFilename = originalFilename.toLowerCase();
            byte[] fileBytes = file.getBytes();
            
            // 先清空所有表数据
            exportUtil.truncateAllTables();
            
            // 根据文件类型调用不同的导入方法
            if (lowerFilename.endsWith(".sql")) {
                exportUtil.importSqlToH2(fileBytes);
                return Result.success("SQL 文件导入成功");
            } else if (lowerFilename.endsWith(".xlsx") || lowerFilename.endsWith(".xls")) {
                exportUtil.importXlsxToH2(fileBytes);
                return Result.success("XLSX 文件导入成功");
            } else {
                return Result.fail("不支持的文件类型，仅支持 .sql 或 .xlsx/.xls 文件");
            }
        } catch (SQLException e) {
            return Result.fail("导入失败: " + e.getMessage());
        } catch (IOException e) {
            return Result.fail("读取文件失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.fail("导入失败: " + e.getMessage());
        }
    }
}

