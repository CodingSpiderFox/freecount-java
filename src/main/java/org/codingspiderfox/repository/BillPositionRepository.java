package org.codingspiderfox.repository;

import org.codingspiderfox.domain.BillPosition;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BillPosition entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BillPositionRepository extends JpaRepository<BillPosition, Long>, JpaSpecificationExecutor<BillPosition> {
}
