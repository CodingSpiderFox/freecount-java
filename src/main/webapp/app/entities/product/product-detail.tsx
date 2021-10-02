import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './product.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { DurationFormat } from 'app/shared/DurationFormat';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProductDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const productEntity = useAppSelector(state => state.product.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productDetailsHeading">
          <Translate contentKey="freecountApp.product.detail.title">Product</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{productEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="freecountApp.product.title">Title</Translate>
            </span>
          </dt>
          <dd>{productEntity.title}</dd>
          <dt>
            <span id="scannerId">
              <Translate contentKey="freecountApp.product.scannerId">Scanner Id</Translate>
            </span>
          </dt>
          <dd>{productEntity.scannerId}</dd>
          <dt>
            <span id="usualDurationFromBuyTillExpire">
              <Translate contentKey="freecountApp.product.usualDurationFromBuyTillExpire">Usual Duration From Buy Till Expire</Translate>
            </span>
          </dt>
          <dd>
            {productEntity.usualDurationFromBuyTillExpire ? <DurationFormat value={productEntity.usualDurationFromBuyTillExpire} /> : null}{' '}
            ({productEntity.usualDurationFromBuyTillExpire})
          </dd>
          <dt>
            <span id="expireMeansBad">
              <Translate contentKey="freecountApp.product.expireMeansBad">Expire Means Bad</Translate>
            </span>
          </dt>
          <dd>{productEntity.expireMeansBad ? 'true' : 'false'}</dd>
          <dt>
            <span id="defaultPrice">
              <Translate contentKey="freecountApp.product.defaultPrice">Default Price</Translate>
            </span>
          </dt>
          <dd>{productEntity.defaultPrice}</dd>
        </dl>
        <Button tag={Link} to="/product" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/product/${productEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductDetail;
