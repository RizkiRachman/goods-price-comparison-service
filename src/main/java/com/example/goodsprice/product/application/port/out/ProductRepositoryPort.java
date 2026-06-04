package com.example.goodsprice.product.application.port.out;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepositoryPort extends GenericRepositoryPort<ProductDomain, Long> {

  ProductDomain findByName(String name);

  List<ProductDomain> searchByName(String name);

  List<ProductDomain> findAllByNames(List<String> names);

  boolean existsByName(String name);

  List<ProductDomain> findAll();

  PageResponse<ProductDomain> search(ProductSearchCriteria criteria);

  List<ProductDomain> findProductsNeedingSummaryUpdate(int limit);

  void updateSummaryLastCalculated(Long productId, LocalDateTime timestamp);

  void updateSummaryLastCalculated(List<Long> productIds, LocalDateTime timestamp);

  void updateLastPriceUpdate(Long productId, LocalDateTime timestamp);
}
