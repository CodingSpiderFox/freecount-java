import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './finance-account.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const FinanceAccountDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const financeAccountEntity = useAppSelector(state => state.financeAccount.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="financeAccountDetailsHeading">
          <Translate contentKey="freecountApp.financeAccount.detail.title">FinanceAccount</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{financeAccountEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="freecountApp.financeAccount.title">Title</Translate>
            </span>
          </dt>
          <dd>{financeAccountEntity.title}</dd>
          <dt>
            <span id="currentBalance">
              <Translate contentKey="freecountApp.financeAccount.currentBalance">Current Balance</Translate>
            </span>
          </dt>
          <dd>{financeAccountEntity.currentBalance}</dd>
          <dt>
            <Translate contentKey="freecountApp.financeAccount.owner">Owner</Translate>
          </dt>
          <dd>{financeAccountEntity.owner ? financeAccountEntity.owner.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/finance-account" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/finance-account/${financeAccountEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FinanceAccountDetail;
