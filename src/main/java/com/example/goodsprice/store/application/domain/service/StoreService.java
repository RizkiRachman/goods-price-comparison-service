package com.example.goodsprice.store.application.domain.service;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.common.service.AbstractGenericService;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.StoreInPort;
import com.example.goodsprice.store.application.port.in.dto.CreateStoreCriteria;
import com.example.goodsprice.store.application.port.in.dto.StoreCriteria;
import com.example.goodsprice.store.application.port.in.dto.UpdateStoreCriteria;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class StoreService extends AbstractGenericService<StoreDomain, Long> implements StoreInPort {

  private final StoreRepositoryPort storeRepository;

  public StoreService(StoreRepositoryPort storeRepository) {
    super("Store", ErrorCodes.STORE_NOT_FOUND);
    this.storeRepository = storeRepository;
  }

  @Override
  protected GenericRepositoryPort<StoreDomain, Long> getRepository() {
    return storeRepository;
  }

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
    store = save(store);
    log.info("Store created: {} (id: {})", criteria.getName(), store.getId());
    return store;
  }

  @Override
  public PageResponse<StoreDomain> findAll(StoreCriteria criteria) {
    return storeRepository.findAll(criteria);
  }

  @Override
  @ActivityLog
  public StoreDomain update(UpdateStoreCriteria criteria) {
    return update(
        criteria.getId(),
        (existing, update) -> {
          existing.setName(update.getName());
          existing.setLocation(update.getLocation());
          existing.setChain(update.getChain());
          existing.setAddress(update.getAddress());
          existing.setLatitude(update.getLatitude());
          existing.setLongitude(update.getLongitude());
          existing.setStatus(update.getStatus());
        },
        criteria);
  }

  @Override
  public List<StoreDomain> findAllById(List<Long> ids) {
    return storeRepository.findAllById(ids);
  }
}
