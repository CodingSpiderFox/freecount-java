package org.codingspiderfox.repository;

import org.codingspiderfox.domain.FinanceTransactions;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the FinanceTransactions entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FinanceTransactionsRepository
    extends JpaRepository<FinanceTransactions, String>, JpaSpecificationExecutor<FinanceTransactions> {}
