import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ProjectSettings from './project-settings';
import ProjectSettingsDetail from './project-settings-detail';
import ProjectSettingsUpdate from './project-settings-update';
import ProjectSettingsDeleteDialog from './project-settings-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ProjectSettingsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ProjectSettingsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ProjectSettingsDetail} />
      <ErrorBoundaryRoute path={match.url} component={ProjectSettings} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ProjectSettingsDeleteDialog} />
  </>
);

export default Routes;
