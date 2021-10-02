import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import FinanceAccount from './finance-account';
import FinanceAccountDetail from './finance-account-detail';
import FinanceAccountUpdate from './finance-account-update';
import FinanceAccountDeleteDialog from './finance-account-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={FinanceAccountUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={FinanceAccountUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={FinanceAccountDetail} />
      <ErrorBoundaryRoute path={match.url} component={FinanceAccount} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={FinanceAccountDeleteDialog} />
  </>
);

export default Routes;
