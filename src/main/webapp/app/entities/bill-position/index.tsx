import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import BillPosition from './bill-position';
import BillPositionDetail from './bill-position-detail';
import BillPositionUpdate from './bill-position-update';
import BillPositionDeleteDialog from './bill-position-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={BillPositionUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={BillPositionUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={BillPositionDetail} />
      <ErrorBoundaryRoute path={match.url} component={BillPosition} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={BillPositionDeleteDialog} />
  </>
);

export default Routes;
