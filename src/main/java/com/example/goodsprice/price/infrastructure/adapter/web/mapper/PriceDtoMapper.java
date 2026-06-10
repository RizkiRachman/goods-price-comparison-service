package com.example.goodsprice.price.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.CheapestPrice;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.PriceRecord;
import com.example.goodsprice.api.model.PriceRecord.AvailabilityEnum;
import com.example.goodsprice.api.model.PriceResult;
import com.example.goodsprice.api.model.PriceResultV2;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceDtoMapper {

  default PriceRecord toPriceRecord(PriceDomain price, StoreDomain store) {
    var record = new PriceRecord();
    record.setId(price.getId());
    record.setProductId(price.getProductId());
    record.setStoreId(price.getStoreId());
    record.setStoreName(store.getName());
    record.setPrice(price.getPrice());
    record.setUnitPrice(price.getUnitPrice());
    record.setDateRecorded(
        ObjectUtils.getOrNull(
            price.getDateRecorded(), d -> d.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()));
    record.setIsPromo(price.getIsPromo());
    record.setAvailability(AvailabilityEnum.IN_STOCK);
    record.setStatus(EntityStatus.COMPLETED);
    return record;
  }

  default PriceResult toResult(PriceDomain price, StoreDomain store) {
    var result = new PriceResult();
    result.setStoreId(price.getStoreId());
    result.setStoreName(ObjectUtils.getOrNull(store, StoreDomain::getName));
    result.setStoreLocation(ObjectUtils.getOrNull(store, StoreDomain::getLocation));
    result.setPrice(price.getPrice());
    result.setUnitPrice(price.getUnitPrice());
    result.setDateRecorded(price.getDateRecorded());
    result.setIsPromo(price.getIsPromo());
    return result;
  }

  default PriceResultV2 toResultV2(PriceDomain price, StoreDomain store) {
    var result = new PriceResultV2();
    result.setStoreId(price.getStoreId());
    result.setStoreName(ObjectUtils.getOrNull(store, StoreDomain::getName));
    result.setPrice(price.getPrice());
    result.setUnitPrice(price.getUnitPrice());
    result.setDateRecorded(
        ObjectUtils.getOrNull(
            price.getDateRecorded(), d -> d.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()));
    result.setIsPromo(price.getIsPromo());
    return result;
  }

  default CheapestPrice toCheapestPrice(PriceDomain cheapest, StoreDomain store) {
    var result = new CheapestPrice();
    result.setStoreName(ObjectUtils.getOrNull(store, StoreDomain::getName));
    result.setPrice(cheapest.getPrice());
    return result;
  }
}
