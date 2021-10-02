import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './stock.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const StockDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const stockEntity = useAppSelector(state => state.stock.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="stockDetailsHeading">
          <Translate contentKey="freecountApp.stock.detail.title">Stock</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{stockEntity.id}</dd>
          <dt>
            <span id="addedTimestamp">
              <Translate contentKey="freecountApp.stock.addedTimestamp">Added Timestamp</Translate>
            </span>
          </dt>
          <dd>
            {stockEntity.addedTimestamp ? <TextFormat value={stockEntity.addedTimestamp} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="storageLocation">
              <Translate contentKey="freecountApp.stock.storageLocation">Storage Location</Translate>
            </span>
          </dt>
          <dd>{stockEntity.storageLocation}</dd>
          <dt>
            <span id="calculatedExpiryTimestamp">
              <Translate contentKey="freecountApp.stock.calculatedExpiryTimestamp">Calculated Expiry Timestamp</Translate>
            </span>
          </dt>
          <dd>
            {stockEntity.calculatedExpiryTimestamp ? (
              <TextFormat value={stockEntity.calculatedExpiryTimestamp} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="manualSetExpiryTimestamp">
              <Translate contentKey="freecountApp.stock.manualSetExpiryTimestamp">Manual Set Expiry Timestamp</Translate>
            </span>
          </dt>
          <dd>
            {stockEntity.manualSetExpiryTimestamp ? (
              <TextFormat value={stockEntity.manualSetExpiryTimestamp} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="freecountApp.stock.product">Product</Translate>
          </dt>
          <dd>{stockEntity.product ? stockEntity.product.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/stock" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/stock/${stockEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StockDetail;
