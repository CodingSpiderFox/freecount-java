import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import FinanceTransactions from './finance-transactions';
import FinanceTransactionsDetail from './finance-transactions-detail';
import FinanceTransactionsUpdate from './finance-transactions-update';
import FinanceTransactionsDeleteDialog from './finance-transactions-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={FinanceTransactionsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={FinanceTransactionsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={FinanceTransactionsDetail} />
      <ErrorBoundaryRoute path={match.url} component={FinanceTransactions} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={FinanceTransactionsDeleteDialog} />
  </>
);

export default Routes;
