import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './bill-position.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const BillPositionDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const billPositionEntity = useAppSelector(state => state.billPosition.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="billPositionDetailsHeading">
          <Translate contentKey="freecountApp.billPosition.detail.title">BillPosition</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{billPositionEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="freecountApp.billPosition.title">Title</Translate>
            </span>
          </dt>
          <dd>{billPositionEntity.title}</dd>
          <dt>
            <span id="cost">
              <Translate contentKey="freecountApp.billPosition.cost">Cost</Translate>
            </span>
          </dt>
          <dd>{billPositionEntity.cost}</dd>
          <dt>
            <span id="order">
              <Translate contentKey="freecountApp.billPosition.order">Order</Translate>
            </span>
          </dt>
          <dd>{billPositionEntity.order}</dd>
          <dt>
            <Translate contentKey="freecountApp.billPosition.bill">Bill</Translate>
          </dt>
          <dd>{billPositionEntity.bill ? billPositionEntity.bill.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/bill-position" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/bill-position/${billPositionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BillPositionDetail;
