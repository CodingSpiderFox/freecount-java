import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IFinanceAccount } from 'app/shared/model/finance-account.model';
import { getEntities as getFinanceAccounts } from 'app/entities/finance-account/finance-account.reducer';
import { getEntity, updateEntity, createEntity, reset } from './finance-transactions.reducer';
import { IFinanceTransactions } from 'app/shared/model/finance-transactions.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const FinanceTransactionsUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const financeAccounts = useAppSelector(state => state.financeAccount.entities);
  const financeTransactionsEntity = useAppSelector(state => state.financeTransactions.entity);
  const loading = useAppSelector(state => state.financeTransactions.loading);
  const updating = useAppSelector(state => state.financeTransactions.updating);
  const updateSuccess = useAppSelector(state => state.financeTransactions.updateSuccess);

  const handleClose = () => {
    props.history.push('/finance-transactions');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getFinanceAccounts({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.executionTimestamp = convertDateTimeToServer(values.executionTimestamp);

    const entity = {
      ...financeTransactionsEntity,
      ...values,
      destinationAccount: financeAccounts.find(it => it.id.toString() === values.destinationAccountId.toString()),
      referenceAccount: financeAccounts.find(it => it.id.toString() === values.referenceAccountId.toString()),
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
          executionTimestamp: displayDefaultDateTime(),
        }
      : {
          ...financeTransactionsEntity,
          executionTimestamp: convertDateTimeFromServer(financeTransactionsEntity.executionTimestamp),
          destinationAccountId: financeTransactionsEntity?.destinationAccount?.id,
          referenceAccountId: financeTransactionsEntity?.referenceAccount?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="freecountApp.financeTransactions.home.createOrEditLabel" data-cy="FinanceTransactionsCreateUpdateHeading">
            <Translate contentKey="freecountApp.financeTransactions.home.createOrEditLabel">Create or edit a FinanceTransactions</Translate>
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
                  id="finance-transactions-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('freecountApp.financeTransactions.executionTimestamp')}
                id="finance-transactions-executionTimestamp"
                name="executionTimestamp"
                data-cy="executionTimestamp"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('freecountApp.financeTransactions.amountAddedToDestinationAccount')}
                id="finance-transactions-amountAddedToDestinationAccount"
                name="amountAddedToDestinationAccount"
                data-cy="amountAddedToDestinationAccount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('freecountApp.financeTransactions.comment')}
                id="finance-transactions-comment"
                name="comment"
                data-cy="comment"
                type="text"
              />
              <ValidatedField
                id="finance-transactions-destinationAccount"
                name="destinationAccountId"
                data-cy="destinationAccount"
                label={translate('freecountApp.financeTransactions.destinationAccount')}
                type="select"
                required
              >
                <option value="" key="0" />
                {financeAccounts
                  ? financeAccounts.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="finance-transactions-referenceAccount"
                name="referenceAccountId"
                data-cy="referenceAccount"
                label={translate('freecountApp.financeTransactions.referenceAccount')}
                type="select"
                required
              >
                <option value="" key="0" />
                {financeAccounts
                  ? financeAccounts.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/finance-transactions" replace color="info">
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

export default FinanceTransactionsUpdate;
