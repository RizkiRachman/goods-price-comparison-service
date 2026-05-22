package com.example.goodsprice.store.application.domain.service;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.common.dto.PageRequest;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.StoreInPort;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService implements StoreInPort {

  private final StoreRepositoryPort storeRepository;

  @Override
  @Transactional
  @ActivityLog
  public StoreDomain create(
      String name,
      String location,
      String chain,
      String address,
      Double latitude,
      Double longitude) {
    var store =
        StoreDomain.builder()
            .name(name)
            .location(location)
            .chain(chain)
            .address(address)
            .latitude(latitude)
            .longitude(longitude)
            .build();
    store = storeRepository.save(store);
    log.info("Store created: {} (id: {})", name, store.getId());
    return store;
  }

  @Override
  public StoreDomain findById(Long id) {
    var store = storeRepository.findById(id);
    if (Objects.isNull(store)) throw NotFoundException.store(id);
    return store;
  }

  @Override
  public PageResponse<StoreDomain> findAll(
      int page,
      int size,
      String sortBy,
      String sortDirection,
      String search,
      String status,
      String chain,
      String location) {
    var pageRequest = new PageRequest(page, size, sortBy, sortDirection);
    return storeRepository.findAll(pageRequest, search, status, chain, location);
  }

  @Override
  @Transactional
  @ActivityLog
  public StoreDomain update(
      Long id,
      String name,
      String location,
      String chain,
      String address,
      Double latitude,
      Double longitude,
      String status) {
    var existing = storeRepository.findById(id);
    if (Objects.isNull(existing)) throw NotFoundException.store(id);
    existing.setName(name);
    existing.setLocation(location);
    existing.setChain(chain);
    existing.setAddress(address);
    existing.setLatitude(latitude);
    existing.setLongitude(longitude);
    existing.setStatus(status);
    existing = storeRepository.save(existing);
    log.info("Store updated: {} (id: {})", existing.getName(), id);
    return existing;
  }

  @Override
  @Transactional
  @ActivityLog
  public void deleteById(Long id) {
    var store = storeRepository.findById(id);
    if (Objects.isNull(store)) throw NotFoundException.store(id);
    storeRepository.deleteById(id);
    log.info("Store deleted: (id: {})", id);
  }
}
