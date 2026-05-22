package com.example.goodsprice.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HexagonalArchitectureTest {

  private static final String BASE = "com.example.goodsprice";
  private static JavaClasses classes;

  @BeforeAll
  static void setup() {
    classes =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE);
  }

  @Test
  void domainMustNotDependOnInfrastructure() {
    noClasses()
        .that()
        .resideInAnyPackage("..application..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..infrastructure..")
        .because("Domain layer must be pure Java with zero infrastructure dependencies")
        .check(classes);
  }

  @Test
  void domainModelsMustNotHaveJpaAnnotations() {
    noClasses()
        .that()
        .resideInAnyPackage("..domain.model..")
        .should()
        .beAnnotatedWith(Entity.class)
        .orShould()
        .beAnnotatedWith(Table.class)
        .orShould()
        .beAnnotatedWith(Column.class)
        .orShould()
        .beAnnotatedWith(Id.class)
        .because(
            "Domain models are pure POJOs with @Builder/@Getter/@Setter — JPA belongs in"
                + " infrastructure entities")
        .check(classes);
  }

  @Test
  void portsMustNotReturnOptional() {
    methods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAnyPackage("..port..")
        .and()
        .arePublic()
        .should()
        .notHaveRawReturnType(Optional.class)
        .because("Ports return nullable, never Optional. Unwrap Optional at adapter boundary")
        .check(classes);
  }

  @Test
  void entitiesMustNotUseJpaRelationshipAnnotations() {
    noClasses()
        .that()
        .resideInAnyPackage("..persistence.entity..")
        .should()
        .beAnnotatedWith(ManyToOne.class)
        .orShould()
        .beAnnotatedWith(OneToMany.class)
        .orShould()
        .beAnnotatedWith(OneToOne.class)
        .orShould()
        .beAnnotatedWith(ManyToMany.class)
        .orShould()
        .beAnnotatedWith(JoinColumn.class)
        .because("FK columns must be stored as primitives (UUID, Long), not JPA relationships")
        .check(classes);
  }

  @Test
  void layeredArchitectureShouldRespectHexagonalBoundaries() {
    layeredArchitecture()
        .consideringAllDependencies()
        .layer("Domain")
        .definedBy("..application.domain..")
        .layer("Ports")
        .definedBy("..application.port..")
        .layer("Exceptions")
        .definedBy("..application.exception..")
        .layer("Infrastructure")
        .definedBy("..infrastructure..")
        .layer("Common")
        .definedBy("..common..")
        .whereLayer("Domain")
        .mayOnlyBeAccessedByLayers("Ports", "Infrastructure")
        .whereLayer("Ports")
        .mayOnlyBeAccessedByLayers("Domain", "Infrastructure")
        .whereLayer("Exceptions")
        .mayOnlyBeAccessedByLayers("Domain", "Infrastructure", "Common")
        .whereLayer("Infrastructure")
        .mayNotBeAccessedByAnyLayer()
        .ignoreDependency(
            com.example.goodsprice.Application.class,
            com.example.goodsprice.llm.infrastructure.config.LlmProperties.class)
        .because("Hexagonal architecture: domain is innermost, infrastructure depends on ports")
        .check(classes);
  }

  @Test
  void domainServicesMustBeAnnotatedWithService() {
    classes()
        .that()
        .resideInAnyPackage("..domain.service..")
        .and()
        .haveSimpleNameEndingWith("Service")
        .should()
        .beAnnotatedWith(org.springframework.stereotype.Service.class)
        .because("Domain services must be @Service annotated")
        .check(classes);
  }

  @Test
  void objectUtilsMustNotBeExtendedWithNewMethods() {
    var allowed = Set.of("defaultIfNull", "getOrNull", "getOrDefault");
    var actual =
        classes.stream()
            .filter(c -> c.getName().endsWith(".ObjectUtils"))
            .flatMap(c -> c.getMethods().stream())
            .map(m -> m.getName())
            .filter(n -> !n.equals("<init>"))
            .collect(Collectors.toSet());
    org.junit.jupiter.api.Assertions.assertTrue(
        allowed.containsAll(actual) && actual.containsAll(allowed),
        "ObjectUtils is sealed — no new methods. Found: " + actual);
  }

  @Test
  void repositoryAdaptersMustBeAnnotatedWithComponent() {
    classes()
        .that()
        .haveSimpleNameEndingWith("RepositoryAdapter")
        .should()
        .beAnnotatedWith(org.springframework.stereotype.Component.class)
        .because("Repository adapters must be @Component annotated")
        .check(classes);
  }
}
