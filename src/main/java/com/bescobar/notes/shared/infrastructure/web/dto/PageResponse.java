package com.bescobar.notes.shared.infrastructure.web.dto;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.data.domain.Page;

@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Intentional list exposure in page response DTO")
public class PageResponse<T> {

    private final List<T> content;
    private final int totalPages;
    private final long numberOfElements;
    private final boolean hasNext;
    private final boolean hasPrevious;

    private PageResponse(List<T> content, int totalPages, long numberOfElements,
                         boolean hasNext, boolean hasPrevious) {
        this.content = content;
        this.totalPages = totalPages;
        this.numberOfElements = numberOfElements;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.hasNext(),
                page.hasPrevious());
    }

    public List<T> getContent() {
        return content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getNumberOfElements() {
        return numberOfElements;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }
}
