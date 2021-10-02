import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './finance-transactions.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const FinanceTransactionsDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const financeTransactionsEntity = useAppSelector(state => state.financeTransactions.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="financeTransactionsDetailsHeading">
          <Translate contentKey="freecountApp.financeTransactions.detail.title">FinanceTransactions</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{financeTransactionsEntity.id}</dd>
          <dt>
            <span id="executionTimestamp">
              <Translate contentKey="freecountApp.financeTransactions.executionTimestamp">Execution Timestamp</Translate>
            </span>
          </dt>
          <dd>
            {financeTransactionsEntity.executionTimestamp ? (
              <TextFormat value={financeTransactionsEntity.executionTimestamp} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="amountAddedToDestinationAccount">
              <Translate contentKey="freecountApp.financeTransactions.amountAddedToDestinationAccount">
                Amount Added To Destination Account
              </Translate>
            </span>
          </dt>
          <dd>{financeTransactionsEntity.amountAddedToDestinationAccount}</dd>
          <dt>
            <span id="comment">
              <Translate contentKey="freecountApp.financeTransactions.comment">Comment</Translate>
            </span>
          </dt>
          <dd>{financeTransactionsEntity.comment}</dd>
          <dt>
            <Translate contentKey="freecountApp.financeTransactions.destinationAccount">Destination Account</Translate>
          </dt>
          <dd>{financeTransactionsEntity.destinationAccount ? financeTransactionsEntity.destinationAccount.id : ''}</dd>
          <dt>
            <Translate contentKey="freecountApp.financeTransactions.referenceAccount">Reference Account</Translate>
          </dt>
          <dd>{financeTransactionsEntity.referenceAccount ? financeTransactionsEntity.referenceAccount.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/finance-transactions" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/finance-transactions/${financeTransactionsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FinanceTransactionsDetail;
