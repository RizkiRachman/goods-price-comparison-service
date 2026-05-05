package com.example.goodsprice.store.application.domain.service;

import com.example.goodsprice.product.application.port.in.StoreLookupInPort;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service that implements StoreLookupInPort to provide store lookup functionality to the product
 * service.
 */
@Service
@RequiredArgsConstructor
public class StoreLookupService implements StoreLookupInPort {

  private final StoreRepositoryPort storeRepository;

  @Override
  public List<Long> findStoreIdsByName(String name) {
    if (Objects.isNull(name) || name.isBlank()) {
      return List.of();
    }
    return storeRepository.findByName(name).stream()
        .map(s -> s.getId())
        .filter(Objects::nonNull)
        .toList();
  }
}
