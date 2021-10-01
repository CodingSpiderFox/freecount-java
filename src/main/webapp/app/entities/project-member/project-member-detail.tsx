import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './project-member.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProjectMemberDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const projectMemberEntity = useAppSelector(state => state.projectMember.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="projectMemberDetailsHeading">
          <Translate contentKey="freecountApp.projectMember.detail.title">ProjectMember</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{projectMemberEntity.id}</dd>
          <dt>
            <span id="additionalProjectPermissions">
              <Translate contentKey="freecountApp.projectMember.additionalProjectPermissions">Additional Project Permissions</Translate>
            </span>
          </dt>
          <dd>{projectMemberEntity.additionalProjectPermissions}</dd>
          <dt>
            <Translate contentKey="freecountApp.projectMember.user">User</Translate>
          </dt>
          <dd>{projectMemberEntity.user ? projectMemberEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="freecountApp.projectMember.project">Project</Translate>
          </dt>
          <dd>{projectMemberEntity.project ? projectMemberEntity.project.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/project-member" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/project-member/${projectMemberEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProjectMemberDetail;
