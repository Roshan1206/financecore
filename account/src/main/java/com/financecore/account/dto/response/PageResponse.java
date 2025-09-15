package com.financecore.account.dto.response;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Custom page response so that we don't get unnecessary/repeated data.
 *
 * @author Roshan
 */
@Data
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private int totalPage;
    private long totalElements;
    private boolean first;
    private boolean last;
    private boolean empty;

    public PageResponse(Page<T> page){
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalPage = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }
}
