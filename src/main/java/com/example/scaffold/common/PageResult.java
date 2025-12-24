package com.example.scaffold.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {
    
    @Schema(description = "数据列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<T> data;
    
    @Schema(description = "分页信息", requiredMode = Schema.RequiredMode.REQUIRED)
    private Pagination pagination;
    
    @Data
    @Accessors(chain = true)
    @Schema(description = "分页信息")
    public static class Pagination implements Serializable {
        @Schema(description = "当前页码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long page;
        
        @Schema(description = "每页数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
        private Long size;
        
        @Schema(description = "总记录数", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
        private Long total;
        
        @Schema(description = "总页数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
        private Long totalPages;
    }
    
    public static <T> PageResult<T> of(List<T> data, Long page, Long size, Long total) {
        PageResult<T> result = new PageResult<>();
        result.setData(data);
        
        Pagination pagination = new Pagination();
        pagination.setPage(page);
        pagination.setSize(size);
        pagination.setTotal(total);
        pagination.setTotalPages((total + size - 1) / size);
        
        result.setPagination(pagination);
        return result;
    }
}

