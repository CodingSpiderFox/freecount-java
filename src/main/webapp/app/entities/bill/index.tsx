import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Bill from './bill';
import BillDetail from './bill-detail';
import BillUpdate from './bill-update';
import BillDeleteDialog from './bill-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={BillUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={BillUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={BillDetail} />
      <ErrorBoundaryRoute path={match.url} component={Bill} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={BillDeleteDialog} />
  </>
);

export default Routes;
