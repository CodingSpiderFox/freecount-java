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
import Stock from './stock';
import FinanceAccount from './finance-account';
import FinanceTransactions from './finance-transactions';
import ProjectMemberRole from './project-member-role';
import ProjectMemberPermission from './project-member-permission';
import ProjectMemberRoleAssignment from './project-member-role-assignment';
import ProjectMemberPermissionAssignment from './project-member-permission-assignment';
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
      <ErrorBoundaryRoute path={`${match.url}stock`} component={Stock} />
      <ErrorBoundaryRoute path={`${match.url}finance-account`} component={FinanceAccount} />
      <ErrorBoundaryRoute path={`${match.url}finance-transactions`} component={FinanceTransactions} />
      <ErrorBoundaryRoute path={`${match.url}project-member-role`} component={ProjectMemberRole} />
      <ErrorBoundaryRoute path={`${match.url}project-member-permission`} component={ProjectMemberPermission} />
      <ErrorBoundaryRoute path={`${match.url}project-member-role-assignment`} component={ProjectMemberRoleAssignment} />
      <ErrorBoundaryRoute path={`${match.url}project-member-permission-assignment`} component={ProjectMemberPermissionAssignment} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
