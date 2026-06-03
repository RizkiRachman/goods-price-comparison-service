package com.example.goodsprice.unit.infrastructure.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.application.domain.model.UnitType;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MethodName")
class UnitDtoMapperTest {

  private final UnitDtoMapper mapper = new UnitDtoMapper();

  @Test
  void shouldMapToApiUnit() {
    var domain =
        UnitDomain.builder()
            .id("KG")
            .name("Kilogram")
            .symbol("kg")
            .type(UnitType.WEIGHT)
            .description("Unit of mass")
            .status(EntityStatus.APPROVED.getValue())
            .build();

    var result = mapper.toApiUnit(domain);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("KG");
    assertThat(result.getType()).isEqualTo(com.example.goodsprice.api.model.Unit.TypeEnum.WEIGHT);
    assertThat(result.getStatus()).isEqualTo(EntityStatus.APPROVED);
  }

  @Test
  void shouldReturnNullForNullInput() {
    assertThat(mapper.toApiUnit(null)).isNull();
  }
}
