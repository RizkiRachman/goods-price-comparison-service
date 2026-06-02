package com.example.goodsprice.store.application.domain.service;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.StoreInPort;
import com.example.goodsprice.store.application.port.in.dto.CreateStoreCriteria;
import com.example.goodsprice.store.application.port.in.dto.StoreCriteria;
import com.example.goodsprice.store.application.port.in.dto.UpdateStoreCriteria;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService implements StoreInPort {

  private static final Logger LOG = LoggerFactory.getLogger(StoreService.class);

  private final StoreRepositoryPort storeRepository;

  @Override
  @Transactional
  @ActivityLog
  public StoreDomain create(CreateStoreCriteria criteria) {
    var store =
        StoreDomain.builder()
            .name(criteria.getName())
            .location(criteria.getLocation())
            .chain(criteria.getChain())
            .address(criteria.getAddress())
            .latitude(criteria.getLatitude())
            .longitude(criteria.getLongitude())
            .build();
    store = storeRepository.save(store);
    LOG.info("Store created: {} (id: {})", criteria.getName(), store.getId());
    return store;
  }

  @Override
  public StoreDomain findById(Long id) {
    var store = storeRepository.findById(id);
    if (Objects.isNull(store)) throw NotFoundException.store(id);
    return store;
  }

  @Override
  public PageResponse<StoreDomain> findAll(StoreCriteria criteria) {
    return storeRepository.findAll(criteria);
  }

  @Override
  @Transactional
  @ActivityLog
  public StoreDomain update(UpdateStoreCriteria criteria) {
    var existing = storeRepository.findById(criteria.getId());
    if (Objects.isNull(existing)) throw NotFoundException.store(criteria.getId());
    existing.setName(criteria.getName());
    existing.setLocation(criteria.getLocation());
    existing.setChain(criteria.getChain());
    existing.setAddress(criteria.getAddress());
    existing.setLatitude(criteria.getLatitude());
    existing.setLongitude(criteria.getLongitude());
    existing.setStatus(criteria.getStatus());
    existing = storeRepository.save(existing);
    LOG.info("Store updated: {} (id: {})", existing.getName(), criteria.getId());
    return existing;
  }

  @Override
  public List<StoreDomain> findAllById(List<Long> ids) {
    return storeRepository.findAllById(ids);
  }

  @Override
  @Transactional
  @ActivityLog
  public void deleteById(Long id) {
    var store = storeRepository.findById(id);
    if (Objects.isNull(store)) throw NotFoundException.store(id);
    storeRepository.deleteById(id);
    LOG.info("Store deleted: (id: {})", id);
  }
}
