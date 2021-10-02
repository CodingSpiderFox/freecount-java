import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ProjectMemberPermission from './project-member-permission';
import ProjectMemberPermissionDetail from './project-member-permission-detail';
import ProjectMemberPermissionUpdate from './project-member-permission-update';
import ProjectMemberPermissionDeleteDialog from './project-member-permission-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ProjectMemberPermissionUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ProjectMemberPermissionUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ProjectMemberPermissionDetail} />
      <ErrorBoundaryRoute path={match.url} component={ProjectMemberPermission} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ProjectMemberPermissionDeleteDialog} />
  </>
);

export default Routes;
