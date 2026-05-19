package com.example.goodsprice.product.application.domain.service;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.common.util.PaginationUtils;
import com.example.goodsprice.common.util.SortingUtils;
import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import com.example.goodsprice.product.application.exception.ProductNotFoundException;
import com.example.goodsprice.product.application.port.in.PriceSummaryInPort;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.product.application.port.in.ProductPriceQueryInPort;
import com.example.goodsprice.product.application.port.in.StoreLookupInPort;
import com.example.goodsprice.product.application.port.out.ProductRepositoryPort;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService implements ProductInPort {

  private final ProductRepositoryPort productRepository;
  private final ProductComparators comparators;
  private final PriceSummaryInPort priceSummaryInPort;
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
  public ProductDomain findById(Long id) {
    var product = productRepository.findById(id);
    if (Objects.isNull(product)) throw new ProductNotFoundException(id);
    return product;
  }

  @Override
  public ProductDomain findByName(String name) {
    var product = productRepository.findByName(name);
    if (Objects.isNull(product)) throw new ProductNotFoundException(name);
    return product;
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
    return search(criteria, false);
  }

  @Override
  public PageResponse<ProductDomain> search(ProductSearchCriteria criteria, boolean includePrice) {
    var products = productRepository.findAll();

    if (criteria.hasSearch()) {
      products = filterBySearch(products, criteria.getSearch());
    }

    if (criteria.hasCategory()) {
      products = filterByCategory(products, criteria.getCategory());
    }

    if (criteria.hasBrand()) {
      products = filterByBrand(products, criteria.getBrand());
    }

    if (criteria.hasStatus()) {
      products = filterByStatus(products, criteria.getStatus());
    }

    if (criteria.hasStoreId()) {
      products = filterByStore(products, criteria);
    }

    Comparator<ProductDomain> comparator = comparators.resolve(criteria.getSortBy());
    products = SortingUtils.sort(products, comparator, criteria.getSortDirection());

    var paginated = PaginationUtils.paginate(products, criteria.getPage(), criteria.getSize());

    if (includePrice && !paginated.content().isEmpty()) {
      populatePriceSummaries(paginated.content());
    }

    return paginated;
  }

  private void populatePriceSummaries(List<ProductDomain> products) {
    Set<Long> productIds =
        products.stream()
            .map(ProductDomain::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    if (productIds.isEmpty()) {
      return;
    }

    List<ProductPriceSummary> summaries = priceSummaryInPort.findByProductIds(productIds);

    Map<Long, ProductPriceSummary> summaryMap =
        summaries.stream()
            .filter(s -> s.getProductId() != null)
            .collect(Collectors.toMap(ProductPriceSummary::getProductId, Function.identity()));

    products.forEach(
        product -> {
          ProductPriceSummary summary = summaryMap.get(product.getId());
          if (summary != null) {
            product.setAvgPrice(summary.getAvgPrice());
            product.setMinPrice(summary.getMinPrice());
            product.setMaxPrice(summary.getMaxPrice());
            product.setPriceUpdatedAt(summary.getLastCalculatedAt());
          }
        });
  }

  private List<ProductDomain> filterBySearch(List<ProductDomain> products, String search) {
    var pattern = search.toLowerCase(Locale.ROOT);
    return products.stream().filter(matchesSearchPattern(pattern)).toList();
  }

  private Predicate<ProductDomain> matchesSearchPattern(String pattern) {
    return p ->
        matchesIgnoreCase(p.getName(), pattern)
            || matchesIgnoreCase(p.getCategory(), pattern)
            || matchesIgnoreCase(p.getBrand(), pattern);
  }

  private boolean matchesIgnoreCase(String value, String pattern) {
    return ObjectUtils.getOrDefault(
        value, v -> v.toLowerCase(Locale.ROOT).contains(pattern), false);
  }

  private List<ProductDomain> filterByCategory(List<ProductDomain> products, String category) {
    return products.stream().filter(p -> Objects.equals(p.getCategory(), category)).toList();
  }

  private List<ProductDomain> filterByBrand(List<ProductDomain> products, String brand) {
    return products.stream().filter(p -> Objects.equals(p.getBrand(), brand)).toList();
  }

  private List<ProductDomain> filterByStatus(List<ProductDomain> products, String status) {
    return products.stream().filter(p -> Objects.equals(p.getStatus(), status)).toList();
  }

  private List<ProductDomain> filterByStore(
      List<ProductDomain> products, ProductSearchCriteria criteria) {
    List<Long> storeIds;

    if (criteria.isStoreIdNumeric()) {

      storeIds = List.of(criteria.getStoreIdAsLong());
    } else {

      storeIds = storeLookupInPort.findStoreIdsByName(criteria.getStoreId());
      if (storeIds.isEmpty()) {
        return List.of();
      }
    }

    List<Long> productIdsAtStore = productPriceQueryInPort.findProductIdsByStoreIds(storeIds);
    if (productIdsAtStore.isEmpty()) {
      return List.of();
    }

    return products.stream().filter(p -> productIdsAtStore.contains(p.getId())).toList();
  }

  @Override
  @Transactional
  @ActivityLog
  public ProductDomain update(Long id, String name, String category, String brand, String unit) {
    var existing = productRepository.findById(id);
    if (Objects.isNull(existing)) throw new ProductNotFoundException(id);
    existing.setName(name);
    existing.setCategory(category);
    existing.setBrand(brand);
    existing.setUnit(unit);
    existing = productRepository.save(existing);
    log.info("Product updated: {} (id: {})", existing.getName(), id);
    return existing;
  }

  @Override
  @Transactional
  @ActivityLog
  public void deleteById(Long id) {
    var product = productRepository.findById(id);
    if (Objects.isNull(product)) throw new ProductNotFoundException(id);
    productRepository.deleteById(id);
    log.info("Product deleted: (id: {})", id);
  }
}
