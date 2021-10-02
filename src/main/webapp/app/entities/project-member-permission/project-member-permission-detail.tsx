import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './project-member-permission.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProjectMemberPermissionDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const projectMemberPermissionEntity = useAppSelector(state => state.projectMemberPermission.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="projectMemberPermissionDetailsHeading">
          <Translate contentKey="freecountApp.projectMemberPermission.detail.title">ProjectMemberPermission</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{projectMemberPermissionEntity.id}</dd>
          <dt>
            <span id="createdTimestamp">
              <Translate contentKey="freecountApp.projectMemberPermission.createdTimestamp">Created Timestamp</Translate>
            </span>
          </dt>
          <dd>
            {projectMemberPermissionEntity.createdTimestamp ? (
              <TextFormat value={projectMemberPermissionEntity.createdTimestamp} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="projectMemberPermission">
              <Translate contentKey="freecountApp.projectMemberPermission.projectMemberPermission">Project Member Permission</Translate>
            </span>
          </dt>
          <dd>{projectMemberPermissionEntity.projectMemberPermission}</dd>
        </dl>
        <Button tag={Link} to="/project-member-permission" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/project-member-permission/${projectMemberPermissionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProjectMemberPermissionDetail;
