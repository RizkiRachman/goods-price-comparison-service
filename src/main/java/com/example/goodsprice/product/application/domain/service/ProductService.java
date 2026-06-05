package com.example.goodsprice.product.application.domain.service;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.util.ProductNameUtils;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.product.application.port.in.ProductInPort.ProductCreateItem;
import com.example.goodsprice.product.application.port.in.ProductPriceQueryInPort;
import com.example.goodsprice.product.application.port.in.StoreLookupInPort;
import com.example.goodsprice.product.application.port.out.ProductRepositoryPort;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService implements ProductInPort {

  private final ProductRepositoryPort productRepository;
  private final ProductPriceQueryInPort productPriceQueryInPort;
  private final StoreLookupInPort storeLookupInPort;

  @Override
  @Transactional
  @ActivityLog
  public ProductDomain create(String name, String category, String brand, String unit) {
    var product =
        ProductDomain.builder().name(name).category(category).brand(brand).unit(unit).build();
    product = productRepository.save(product);
    log.info("Product created: {} (id: {})", name, product.getId());
    return product;
  }

  @Override
  @Transactional
  @ActivityLog
  public ProductDomain createIfNotExist(String name, String category, String unit) {
    var existing = productRepository.findByName(name);
    if (Objects.nonNull(existing)) {
      log.debug("Product already exists: {} (id: {})", name, existing.getId());
      return existing;
    }

    var product = ProductDomain.builder().name(name).category(category).unit(unit).build();
    product = productRepository.save(product);
    log.info("Product created: {} (id: {})", name, product.getId());
    return product;
  }

  @Override
  @Transactional
  @ActivityLog
  public Map<String, ProductDomain> createIfNotExistBatch(List<ProductCreateItem> items) {
    if (Objects.isNull(items) || items.isEmpty()) return Map.of();

    // Collect unique cleaned names with their category/unit info
    var nameToInfo = new LinkedHashMap<String, ProductCreateItem>();
    for (var item : items) {
      var cleanedName = ProductNameUtils.cleanProductName(item.name(), item.unit());
      if (!nameToInfo.containsKey(cleanedName)) {
        nameToInfo.put(cleanedName, item);
      }
    }

    // Find existing products
    var uniqueNames = new ArrayList<>(nameToInfo.keySet());
    var existing = productRepository.findAllByNames(uniqueNames);
    var result = new HashMap<String, ProductDomain>();
    for (var product : existing) {
      result.put(product.getName(), product);
    }

    // Create missing products
    var productsToSave = new ArrayList<ProductDomain>();
    for (var entry : nameToInfo.entrySet()) {
      var name = entry.getKey();
      if (!result.containsKey(name)) {
        var info = entry.getValue();
        productsToSave.add(
            ProductDomain.builder().name(name).category(info.category()).unit(info.unit()).build());
      }
    }

    if (!productsToSave.isEmpty()) {
      for (var product : productsToSave) {
        var saved = productRepository.save(product);
        result.put(saved.getName(), saved);
      }
      log.info("Created {} new products in batch", productsToSave.size());
    }

    return result;
  }

  @Override
  public ProductDomain findById(Long id) {
    var product = productRepository.findById(id);
    if (Objects.isNull(product)) throw NotFoundException.product(id);
    return product;
  }

  @Override
  public ProductDomain findByName(String name) {
    return productRepository.findByName(name);
  }

  @Override
  public List<ProductDomain> searchByName(String name) {
    return productRepository.searchByName(name);
  }

  @Override
  public List<ProductDomain> findAllByNames(List<String> names) {
    return productRepository.findAllByNames(names);
  }

  @Override
  public List<ProductDomain> findAll() {
    return productRepository.findAll();
  }

  @Override
  public PageResponse<ProductDomain> search(ProductSearchCriteria criteria) {
    if (criteria.hasStoreId()) {
      List<Long> storeIds;

      if (criteria.isStoreIdNumeric()) {
        storeIds = List.of(criteria.getStoreIdAsLong());
      } else {
        var foundIds = storeLookupInPort.findStoreIdsByName(criteria.getStoreId());
        storeIds = Objects.nonNull(foundIds) ? foundIds : List.of();
        if (storeIds.isEmpty()) {
          return PageResponse.of(List.of(), criteria.getPage(), criteria.getSize(), 0);
        }
      }

      var productIdsAtStore = productPriceQueryInPort.findProductIdsByStoreIds(storeIds);
      var productIds = Objects.nonNull(productIdsAtStore) ? productIdsAtStore : List.<Long>of();

      if (productIds.isEmpty()) {
        return PageResponse.of(List.of(), criteria.getPage(), criteria.getSize(), 0);
      }

      criteria.setProductIds(productIds);
    }

    return productRepository.search(criteria);
  }

  @Override
  @Transactional
  @ActivityLog
  public ProductDomain update(Long id, String name, String category, String brand, String unit) {
    var existing = productRepository.findById(id);
    if (Objects.isNull(existing)) throw NotFoundException.product(id);
    existing.setName(name);
    existing.setCategory(category);
    existing.setBrand(brand);
    existing.setUnit(unit);
    existing = productRepository.save(existing);
    log.info("Product updated: {} (id: {})", existing.getName(), id);
    return existing;
  }

  @Override
  public void updateLastPriceUpdate(Long productId, LocalDateTime lastPriceUpdate) {
    productRepository.updateLastPriceUpdate(productId, lastPriceUpdate);
  }

  @Override
  @Transactional
  @ActivityLog
  public void deleteById(Long id) {
    var product = productRepository.findById(id);
    if (Objects.isNull(product)) throw NotFoundException.product(id);
    productRepository.deleteById(id);
    log.info("Product deleted: (id: {})", id);
  }
}
