package com.organlink.repository;

import com.organlink.model.entity.OrganType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for OrganTypeRepository
 * Tests MySQL database operations for OrganType entity
 */
@DataJpaTest
@ActiveProfiles("test")
class OrganTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrganTypeRepository organTypeRepository;

    @Test
    void testSaveAndFindOrganType() {
        // Given
        OrganType organType = new OrganType();
        organType.setName("Heart");
        organType.setDescription("Cardiac organ for transplantation");
        organType.setPreservationTimeHours(4);
        organType.setCompatibilityFactors("{\"blood_type_compatible\": true}");
        organType.setIsActive(true);

        // When
        OrganType savedOrganType = organTypeRepository.save(organType);

        // Then
        assertThat(savedOrganType.getId()).isNotNull();
        assertThat(savedOrganType.getName()).isEqualTo("Heart");
        assertThat(savedOrganType.getDescription()).isEqualTo("Cardiac organ for transplantation");
        assertThat(savedOrganType.getPreservationTimeHours()).isEqualTo(4);
        assertThat(savedOrganType.getIsActive()).isTrue();
    }

    @Test
    void testFindByNameIgnoreCase() {
        // Given
        OrganType organType = new OrganType();
        organType.setName("Kidney");
        organType.setDescription("Renal organ for transplantation");
        organType.setPreservationTimeHours(24);
        organType.setIsActive(true);
        entityManager.persistAndFlush(organType);

        // When
        Optional<OrganType> found = organTypeRepository.findByNameIgnoreCase("kidney");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Kidney");
    }

    @Test
    void testFindByIsActiveTrueOrderByNameAsc() {
        // Given
        OrganType heart = new OrganType("Heart", "Cardiac organ", 4);
        heart.setIsActive(true);
        
        OrganType liver = new OrganType("Liver", "Hepatic organ", 12);
        liver.setIsActive(true);
        
        OrganType kidney = new OrganType("Kidney", "Renal organ", 24);
        kidney.setIsActive(false);

        entityManager.persist(heart);
        entityManager.persist(liver);
        entityManager.persist(kidney);
        entityManager.flush();

        // When
        List<OrganType> activeOrganTypes = organTypeRepository.findByIsActiveTrueOrderByNameAsc();

        // Then
        assertThat(activeOrganTypes).hasSize(2);
        assertThat(activeOrganTypes.get(0).getName()).isEqualTo("Heart");
        assertThat(activeOrganTypes.get(1).getName()).isEqualTo("Liver");
    }

    @Test
    void testFindUrgentOrganTypes() {
        // Given
        OrganType heart = new OrganType("Heart", "Cardiac organ", 4);
        heart.setIsActive(true);
        
        OrganType liver = new OrganType("Liver", "Hepatic organ", 12);
        liver.setIsActive(true);
        
        OrganType kidney = new OrganType("Kidney", "Renal organ", 24);
        kidney.setIsActive(true);

        entityManager.persist(heart);
        entityManager.persist(liver);
        entityManager.persist(kidney);
        entityManager.flush();

        // When
        List<OrganType> urgentOrganTypes = organTypeRepository.findUrgentOrganTypes();

        // Then
        assertThat(urgentOrganTypes).hasSize(2);
        assertThat(urgentOrganTypes.get(0).getName()).isEqualTo("Heart");
        assertThat(urgentOrganTypes.get(1).getName()).isEqualTo("Liver");
    }

    @Test
    void testExistsByNameIgnoreCase() {
        // Given
        OrganType organType = new OrganType("Lung", "Pulmonary organ", 6);
        entityManager.persistAndFlush(organType);

        // When & Then
        assertThat(organTypeRepository.existsByNameIgnoreCase("lung")).isTrue();
        assertThat(organTypeRepository.existsByNameIgnoreCase("LUNG")).isTrue();
        assertThat(organTypeRepository.existsByNameIgnoreCase("NonExistent")).isFalse();
    }

    @Test
    void testFindByPreservationTimeRange() {
        // Given
        OrganType heart = new OrganType("Heart", "Cardiac organ", 4);
        heart.setIsActive(true);
        
        OrganType liver = new OrganType("Liver", "Hepatic organ", 12);
        liver.setIsActive(true);
        
        OrganType kidney = new OrganType("Kidney", "Renal organ", 24);
        kidney.setIsActive(true);

        entityManager.persist(heart);
        entityManager.persist(liver);
        entityManager.persist(kidney);
        entityManager.flush();

        // When
        List<OrganType> organTypesInRange = organTypeRepository.findByPreservationTimeRange(5, 20);

        // Then
        assertThat(organTypesInRange).hasSize(1);
        assertThat(organTypesInRange.get(0).getName()).isEqualTo("Liver");
    }
}
