package com.example.goodsprice.store.infrastructure.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Store;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@SuppressWarnings("checkstyle:MethodName")
class StoreDtoMapperTest {

  private final StoreDtoMapper mapper = Mappers.getMapper(StoreDtoMapper.class);

  @Test
  void shouldMapToApiStore() {
    var domain =
        StoreDomain.builder()
            .id(1L)
            .name("Toko Segar")
            .location("Jakarta")
            .chain("Chain A")
            .address("Jl. Sudirman")
            .latitude(1.2345)
            .longitude(6.7890)
            .status(EntityStatus.APPROVED.getValue())
            .build();

    Store result = mapper.toApiStore(domain);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("Toko Segar");
    assertThat(result.getStatus()).isEqualTo(EntityStatus.APPROVED);
  }

  @Test
  void shouldMapNullStatusToNull() {
    var domain =
        StoreDomain.builder().id(2L).name("Toko Lain").location("Bandung").status(null).build();

    Store result = mapper.toApiStore(domain);
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isNull();
  }
}
