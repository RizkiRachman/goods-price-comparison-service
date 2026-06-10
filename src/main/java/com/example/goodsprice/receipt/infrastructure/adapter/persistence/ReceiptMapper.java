package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import com.example.goodsprice.common.persistence.EntityMapperConfig;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptEntity;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = EntityMapperConfig.class)
public interface ReceiptMapper {

  String TOTAL_AMOUNT = "totalAmount";

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "processedAt", ignore = true)
  @Mapping(target = TOTAL_AMOUNT, source = TOTAL_AMOUNT, qualifiedByName = "bigDecimalToDouble")
  ReceiptEntity toEntity(ReceiptDomain domain);

  @Mapping(target = TOTAL_AMOUNT, source = TOTAL_AMOUNT, qualifiedByName = "doubleToBigDecimal")
  ReceiptDomain toDomain(ReceiptEntity entity);

  @Named("doubleToBigDecimal")
  default BigDecimal doubleToBigDecimal(Double value) {
    if (value == null) return null;
    return BigDecimal.valueOf(value);
  }

  @Named("bigDecimalToDouble")
  default Double bigDecimalToDouble(BigDecimal value) {
    if (value == null) return null;
    return value.doubleValue();
  }
}
