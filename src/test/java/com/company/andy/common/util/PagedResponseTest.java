package com.company.andy.common.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.PageRequest.of;

class PagedResponseTest {
    @Test
    void should_create_paged_response() {
        PagedResponse<String> response = new PagedResponse<>(List.of("abc"), of(0, 25), 100);
        assertEquals(1, response.getContent().size());
        assertEquals(100, response.getTotalElements());
        assertEquals(4, response.getTotalPages());
        assertEquals(0, response.getPageNumber());
        assertEquals(25, response.getPageSize());
        assertTrue(response.isHasNext());

        PagedResponse<String> middlePage = new PagedResponse<>(List.of("abc"), of(0, 25), 99);
        assertEquals(4, middlePage.getTotalPages());
        assertEquals(0, middlePage.getPageNumber());
        assertEquals(25, middlePage.getPageSize());
        assertTrue(middlePage.isHasNext());

        PagedResponse<String> lastPage = new PagedResponse<>(List.of("abc"), of(3, 25), 100);
        assertEquals(4, lastPage.getTotalPages());
        assertEquals(3, lastPage.getPageNumber());
        assertEquals(25, lastPage.getPageSize());
        assertFalse(lastPage.isHasNext());
    }

    @Test
    void should_create_empty_paged_response() {
        PagedResponse<String> response = PagedResponse.empty(of(0, 25));
        assertEquals(0, response.getContent().size());
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
        assertEquals(0, response.getPageNumber());
        assertEquals(25, response.getPageSize());
        assertFalse(response.isHasNext());
    }

}