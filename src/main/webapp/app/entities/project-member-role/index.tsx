import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ProjectMemberRole from './project-member-role';
import ProjectMemberRoleDetail from './project-member-role-detail';
import ProjectMemberRoleUpdate from './project-member-role-update';
import ProjectMemberRoleDeleteDialog from './project-member-role-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ProjectMemberRoleUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ProjectMemberRoleUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ProjectMemberRoleDetail} />
      <ErrorBoundaryRoute path={match.url} component={ProjectMemberRole} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ProjectMemberRoleDeleteDialog} />
  </>
);

export default Routes;
