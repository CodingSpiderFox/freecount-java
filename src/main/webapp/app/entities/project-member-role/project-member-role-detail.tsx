import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './project-member-role.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProjectMemberRoleDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const projectMemberRoleEntity = useAppSelector(state => state.projectMemberRole.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="projectMemberRoleDetailsHeading">
          <Translate contentKey="freecountApp.projectMemberRole.detail.title">ProjectMemberRole</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{projectMemberRoleEntity.id}</dd>
          <dt>
            <span id="createdTimestamp">
              <Translate contentKey="freecountApp.projectMemberRole.createdTimestamp">Created Timestamp</Translate>
            </span>
          </dt>
          <dd>
            {projectMemberRoleEntity.createdTimestamp ? (
              <TextFormat value={projectMemberRoleEntity.createdTimestamp} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="projectMemberRole">
              <Translate contentKey="freecountApp.projectMemberRole.projectMemberRole">Project Member Role</Translate>
            </span>
          </dt>
          <dd>{projectMemberRoleEntity.projectMemberRole}</dd>
        </dl>
        <Button tag={Link} to="/project-member-role" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/project-member-role/${projectMemberRoleEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProjectMemberRoleDetail;
