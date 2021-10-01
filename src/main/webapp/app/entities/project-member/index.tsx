import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ProjectMember from './project-member';
import ProjectMemberDetail from './project-member-detail';
import ProjectMemberUpdate from './project-member-update';
import ProjectMemberDeleteDialog from './project-member-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ProjectMemberUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ProjectMemberUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ProjectMemberDetail} />
      <ErrorBoundaryRoute path={match.url} component={ProjectMember} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ProjectMemberDeleteDialog} />
  </>
);

export default Routes;
