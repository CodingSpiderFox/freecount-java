import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './project-member-permission-assignment.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProjectMemberPermissionAssignmentDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const projectMemberPermissionAssignmentEntity = useAppSelector(state => state.projectMemberPermissionAssignment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="projectMemberPermissionAssignmentDetailsHeading">
          <Translate contentKey="freecountApp.projectMemberPermissionAssignment.detail.title">ProjectMemberPermissionAssignment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{projectMemberPermissionAssignmentEntity.id}</dd>
          <dt>
            <span id="assignmentTimestamp">
              <Translate contentKey="freecountApp.projectMemberPermissionAssignment.assignmentTimestamp">Assignment Timestamp</Translate>
            </span>
          </dt>
          <dd>
            {projectMemberPermissionAssignmentEntity.assignmentTimestamp ? (
              <TextFormat value={projectMemberPermissionAssignmentEntity.assignmentTimestamp} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="freecountApp.projectMemberPermissionAssignment.projectMember">Project Member</Translate>
          </dt>
          <dd>{projectMemberPermissionAssignmentEntity.projectMember ? projectMemberPermissionAssignmentEntity.projectMember.id : ''}</dd>
          <dt>
            <Translate contentKey="freecountApp.projectMemberPermissionAssignment.projectMemberPermission">
              Project Member Permission
            </Translate>
          </dt>
          <dd>
            {projectMemberPermissionAssignmentEntity.projectMemberPermissions
              ? projectMemberPermissionAssignmentEntity.projectMemberPermissions.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {projectMemberPermissionAssignmentEntity.projectMemberPermissions &&
                    i === projectMemberPermissionAssignmentEntity.projectMemberPermissions.length - 1
                      ? ''
                      : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/project-member-permission-assignment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button
          tag={Link}
          to={`/project-member-permission-assignment/${projectMemberPermissionAssignmentEntity.id}/edit`}
          replace
          color="primary"
        >
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProjectMemberPermissionAssignmentDetail;
