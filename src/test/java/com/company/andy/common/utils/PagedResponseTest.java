package com.company.andy.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.domain.PageRequest.of;

import java.util.List;

import org.junit.jupiter.api.Test;

class PagedResponseTest {
  @Test
  void should_create_paged_response() {
    PagedResponse<String> response = new PagedResponse<>(List.of("abc"), of(0, 25), 100);
    assertEquals(1, response.content().size());
    assertEquals(100, response.totalElements());
    assertEquals(4, response.totalPages());
    assertEquals(0, response.pageNumber());
    assertEquals(25, response.pageSize());
    assertTrue(response.hasNext());

    PagedResponse<String> middlePage = new PagedResponse<>(List.of("abc"), of(0, 25), 99);
    assertEquals(4, middlePage.totalPages());
    assertEquals(0, middlePage.pageNumber());
    assertEquals(25, middlePage.pageSize());
    assertTrue(middlePage.hasNext());

    PagedResponse<String> lastPage = new PagedResponse<>(List.of("abc"), of(3, 25), 100);
    assertEquals(4, lastPage.totalPages());
    assertEquals(3, lastPage.pageNumber());
    assertEquals(25, lastPage.pageSize());
    assertFalse(lastPage.hasNext());
  }

  @Test
  void should_create_empty_paged_response() {
    PagedResponse<String> response = PagedResponse.empty(of(0, 25));
    assertEquals(0, response.content().size());
    assertEquals(0, response.totalElements());
    assertEquals(0, response.totalPages());
    assertEquals(0, response.pageNumber());
    assertEquals(25, response.pageSize());
    assertFalse(response.hasNext());
  }
}