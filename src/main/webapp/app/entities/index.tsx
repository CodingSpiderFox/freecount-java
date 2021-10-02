import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Project from './project';
import ProjectSettings from './project-settings';
import Bill from './bill';
import BillPosition from './bill-position';
import ProjectMember from './project-member';
import Product from './product';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}project`} component={Project} />
      <ErrorBoundaryRoute path={`${match.url}project-settings`} component={ProjectSettings} />
      <ErrorBoundaryRoute path={`${match.url}bill`} component={Bill} />
      <ErrorBoundaryRoute path={`${match.url}bill-position`} component={BillPosition} />
      <ErrorBoundaryRoute path={`${match.url}project-member`} component={ProjectMember} />
      <ErrorBoundaryRoute path={`${match.url}product`} component={Product} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
