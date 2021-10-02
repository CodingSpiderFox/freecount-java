import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ProjectMemberPermissionAssignment from './project-member-permission-assignment';
import ProjectMemberPermissionAssignmentDetail from './project-member-permission-assignment-detail';
import ProjectMemberPermissionAssignmentUpdate from './project-member-permission-assignment-update';
import ProjectMemberPermissionAssignmentDeleteDialog from './project-member-permission-assignment-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ProjectMemberPermissionAssignmentUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ProjectMemberPermissionAssignmentUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ProjectMemberPermissionAssignmentDetail} />
      <ErrorBoundaryRoute path={match.url} component={ProjectMemberPermissionAssignment} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ProjectMemberPermissionAssignmentDeleteDialog} />
  </>
);

export default Routes;
