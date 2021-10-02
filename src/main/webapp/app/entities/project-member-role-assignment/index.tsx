import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ProjectMemberRoleAssignment from './project-member-role-assignment';
import ProjectMemberRoleAssignmentDetail from './project-member-role-assignment-detail';
import ProjectMemberRoleAssignmentUpdate from './project-member-role-assignment-update';
import ProjectMemberRoleAssignmentDeleteDialog from './project-member-role-assignment-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ProjectMemberRoleAssignmentUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ProjectMemberRoleAssignmentUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ProjectMemberRoleAssignmentDetail} />
      <ErrorBoundaryRoute path={match.url} component={ProjectMemberRoleAssignment} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ProjectMemberRoleAssignmentDeleteDialog} />
  </>
);

export default Routes;
