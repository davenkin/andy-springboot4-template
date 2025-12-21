package com.company.andy.common.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
public class PagedResponse<T> {
    @Schema(description = "Paged elements")
    private final List<T> content;

    @Schema(description = "Total number of elements")
    private final long totalElements;

    @Schema(description = "Total number of pages")
    private final long totalPages;

    @Schema(description = "Page number")
    private final int pageNumber;

    @Schema(description = "Page size")
    private final int pageSize;

    @Schema(description = "Whether next page exists")
    private final boolean hasNext;

    public PagedResponse(List<T> content, Pageable pageable, long totalElements) {
        this.content = content;
        this.totalElements = totalElements;
        this.pageNumber = pageable.getPageNumber();
        this.pageSize = pageable.getPageSize();
        this.totalPages = (long) Math.ceil((double) this.totalElements / this.pageSize);
        this.hasNext = this.pageNumber + 1 < this.totalPages;
    }

    public static <T> PagedResponse<T> empty(Pageable pageable) {
        return new PagedResponse<>(List.of(), pageable, 0);
    }

}
