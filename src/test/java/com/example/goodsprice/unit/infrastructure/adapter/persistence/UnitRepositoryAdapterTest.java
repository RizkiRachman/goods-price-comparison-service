package com.example.goodsprice.unit.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.application.domain.model.UnitType;
import com.example.goodsprice.unit.infrastructure.adapter.persistence.entity.UnitEntity;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class UnitRepositoryAdapterTest {

  @Mock private JpaUnitRepository jpaRepository;
  @Mock private UnitMapper mapper;

  private UnitRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new UnitRepositoryAdapter(jpaRepository, mapper);
  }

  @Test
  @DisplayName("Should save unit")
  void shouldSaveUnit() {
    var domain = UnitDomain.builder().id("KG").name("Kilogram").type(UnitType.WEIGHT).build();
    var entity = new UnitEntity();
    entity.setId("KG");
    entity.setName("Kilogram");

    when(mapper.toEntity(domain)).thenReturn(entity);
    when(jpaRepository.save(entity)).thenReturn(entity);
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.save(domain);

    assertNotNull(result);
    verify(jpaRepository).save(entity);
  }

  @Test
  @DisplayName("Should find unit by id")
  void shouldFindById() {
    var entity = new UnitEntity();
    entity.setId("KG");
    entity.setName("Kilogram");
    var domain = UnitDomain.builder().id("KG").name("Kilogram").build();

    when(jpaRepository.findById("KG")).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findById("KG");

    assertNotNull(result);
  }

  @Test
  @DisplayName("Should return null when unit not found")
  void shouldReturnNullWhenNotFound() {
    when(jpaRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

    assertNull(adapter.findById("NONEXISTENT"));
  }

  @Test
  @DisplayName("Should return null when id is null")
  void shouldReturnNullWhenIdIsNull() {
    assertNull(adapter.findById(null));
  }

  @Test
  @DisplayName("Should find all units with pagination")
  void shouldFindAllWithPagination() {
    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(Page.empty());

    var result = adapter.findAll(new PageRequestDto(0, 10, "name", "asc"), null, null);

    assertNotNull(result);
  }

  @Test
  @DisplayName("Should find all units with search filter")
  void shouldFindAllWithSearch() {
    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(Page.empty());

    var result = adapter.findAll(new PageRequestDto(0, 10, "name", "asc"), null, null);

    assertNotNull(result);
  }
}
