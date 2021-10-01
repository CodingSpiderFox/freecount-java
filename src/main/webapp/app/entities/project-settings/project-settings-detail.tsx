import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './project-settings.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProjectSettingsDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const projectSettingsEntity = useAppSelector(state => state.projectSettings.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="projectSettingsDetailsHeading">
          <Translate contentKey="freecountApp.projectSettings.detail.title">ProjectSettings</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{projectSettingsEntity.id}</dd>
          <dt>
            <span id="mustProvideBillCopyByDefault">
              <Translate contentKey="freecountApp.projectSettings.mustProvideBillCopyByDefault">
                Must Provide Bill Copy By Default
              </Translate>
            </span>
          </dt>
          <dd>{projectSettingsEntity.mustProvideBillCopyByDefault ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="freecountApp.projectSettings.project">Project</Translate>
          </dt>
          <dd>{projectSettingsEntity.project ? projectSettingsEntity.project.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/project-settings" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/project-settings/${projectSettingsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProjectSettingsDetail;
