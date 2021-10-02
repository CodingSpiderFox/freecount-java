package org.codingspiderfox.service;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.codingspiderfox.domain.Bill;
import org.codingspiderfox.domain.BillPosition;
import org.codingspiderfox.domain.BillPosition_;
import org.codingspiderfox.repository.BillPositionRepository;
import org.codingspiderfox.repository.BillRepository;
import org.codingspiderfox.service.criteria.BillPositionCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.filter.LongFilter;

@Service
@Transactional
@Slf4j
public class BillCommandHandler {

    @Autowired
    private BillQueryService billQueryService;

    @Autowired
    private BillPositionService billPositionService;

    @Autowired
    private BillPositionQueryService billPositionQueryService;

    @Autowired
    private BillPositionRepository billPositionRepository;

    @Autowired
    private BillRepository billRepository;

    @Transactional
    public void closeBillAndUpdateProjectAndMemberAccountsBalances(Long billId) {
        Bill bill = billRepository.findById(billId).orElseThrow(() -> new IllegalArgumentException("Bill not found"));
        List<BillPosition> positionsForBill = billPositionQueryService.findByBillId(billId);
        Double totalBillAmount = positionsForBill.stream().map(BillPosition::getCost).reduce((double) 0, Double::sum);

        bill.setClosedTimestamp(ZonedDateTime.now());
        bill.setFinalAmount(totalBillAmount);
        billRepository.save(bill);
    }
}
