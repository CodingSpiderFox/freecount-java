package org.codingspiderfox.repository;

import org.codingspiderfox.domain.FinanceAccount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the FinanceAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FinanceAccountRepository extends JpaRepository<FinanceAccount, String>, JpaSpecificationExecutor<FinanceAccount> {}
