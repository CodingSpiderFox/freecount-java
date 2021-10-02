import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IProduct } from 'app/shared/model/product.model';
import { getEntities as getProducts } from 'app/entities/product/product.reducer';
import { getEntity, updateEntity, createEntity, reset } from './stock.reducer';
import { IStock } from 'app/shared/model/stock.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const StockUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const products = useAppSelector(state => state.product.entities);
  const stockEntity = useAppSelector(state => state.stock.entity);
  const loading = useAppSelector(state => state.stock.loading);
  const updating = useAppSelector(state => state.stock.updating);
  const updateSuccess = useAppSelector(state => state.stock.updateSuccess);

  const handleClose = () => {
    props.history.push('/stock');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getProducts({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.addedTimestamp = convertDateTimeToServer(values.addedTimestamp);
    values.calculatedExpiryTimestamp = convertDateTimeToServer(values.calculatedExpiryTimestamp);
    values.manualSetExpiryTimestamp = convertDateTimeToServer(values.manualSetExpiryTimestamp);

    const entity = {
      ...stockEntity,
      ...values,
      product: products.find(it => it.id.toString() === values.productId.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          addedTimestamp: displayDefaultDateTime(),
          calculatedExpiryTimestamp: displayDefaultDateTime(),
          manualSetExpiryTimestamp: displayDefaultDateTime(),
        }
      : {
          ...stockEntity,
          addedTimestamp: convertDateTimeFromServer(stockEntity.addedTimestamp),
          calculatedExpiryTimestamp: convertDateTimeFromServer(stockEntity.calculatedExpiryTimestamp),
          manualSetExpiryTimestamp: convertDateTimeFromServer(stockEntity.manualSetExpiryTimestamp),
          productId: stockEntity?.product?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="freecountApp.stock.home.createOrEditLabel" data-cy="StockCreateUpdateHeading">
            <Translate contentKey="freecountApp.stock.home.createOrEditLabel">Create or edit a Stock</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="stock-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('freecountApp.stock.addedTimestamp')}
                id="stock-addedTimestamp"
                name="addedTimestamp"
                data-cy="addedTimestamp"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('freecountApp.stock.storageLocation')}
                id="stock-storageLocation"
                name="storageLocation"
                data-cy="storageLocation"
                type="text"
              />
              <ValidatedField
                label={translate('freecountApp.stock.calculatedExpiryTimestamp')}
                id="stock-calculatedExpiryTimestamp"
                name="calculatedExpiryTimestamp"
                data-cy="calculatedExpiryTimestamp"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('freecountApp.stock.manualSetExpiryTimestamp')}
                id="stock-manualSetExpiryTimestamp"
                name="manualSetExpiryTimestamp"
                data-cy="manualSetExpiryTimestamp"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="stock-product"
                name="productId"
                data-cy="product"
                label={translate('freecountApp.stock.product')}
                type="select"
                required
              >
                <option value="" key="0" />
                {products
                  ? products.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/stock" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default StockUpdate;
