package com.example.goodsprice.product.application.port.in;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import java.util.List;

public interface ProductInPort {

  ProductDomain create(String name, String category, String brand, String unit);

  ProductDomain createIfNotExist(String name, String category, String unit);

  ProductDomain findById(Long id);

  ProductDomain findByName(String name);

  List<ProductDomain> findAllByNames(List<String> names);

  /**
   * Returns all products without pagination. Use {@link #search(ProductSearchCriteria)} for
   * paginated results.
   */
  List<ProductDomain> findAll();

  /**
   * Searches products with filtering, sorting, and pagination.
   *
   * @param criteria the search criteria
   * @return paginated response containing matching products
   */
  PageResponse<ProductDomain> search(ProductSearchCriteria criteria);

  /**
   * Searches products with filtering, sorting, and pagination. Optionally includes price summary
   * data.
   *
   * @param criteria the search criteria
   * @param includePrice whether to include price summary information
   * @return paginated response containing matching products
   */
  PageResponse<ProductDomain> search(ProductSearchCriteria criteria, boolean includePrice);

  ProductDomain update(Long id, String name, String category, String brand, String unit);

  void deleteById(Long id);
}
