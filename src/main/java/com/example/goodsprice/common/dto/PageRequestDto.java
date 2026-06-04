package com.example.goodsprice.common.dto;

/**
 * Page request with 1-based page numbering from API input.
 *
 * <p>The {@code page} field stores the 1-based page number as received from API callers. Use {@link
 * #toZeroBased()} to convert to 0-based for Spring Data {@code PageRequest}.
 */
public record PageRequestDto(int page, int size, String sortBy, String sortDirection) {

  public PageRequestDto {
    if (page < 0) page = 0;
    if (size <= 0) size = 20;
  }

  /**
   * Returns a 0-based page number for Spring Data PageRequest. Converts from 1-based API input.
   *
   * @return 0-based page number (minimum 0)
   */
  public int toZeroBased() {
    return Math.max(0, page - 1);
  }
}
