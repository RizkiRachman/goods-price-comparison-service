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

  List<ProductDomain> findAll();

  PageResponse<ProductDomain> search(ProductSearchCriteria criteria);

  PageResponse<ProductDomain> search(ProductSearchCriteria criteria, boolean includePrice);

  ProductDomain update(Long id, String name, String category, String brand, String unit);

  void deleteById(Long id);
}
