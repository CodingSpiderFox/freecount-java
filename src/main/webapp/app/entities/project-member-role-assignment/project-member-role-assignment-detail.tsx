import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './project-member-role-assignment.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProjectMemberRoleAssignmentDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const projectMemberRoleAssignmentEntity = useAppSelector(state => state.projectMemberRoleAssignment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="projectMemberRoleAssignmentDetailsHeading">
          <Translate contentKey="freecountApp.projectMemberRoleAssignment.detail.title">ProjectMemberRoleAssignment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{projectMemberRoleAssignmentEntity.id}</dd>
          <dt>
            <span id="assignmentTimestamp">
              <Translate contentKey="freecountApp.projectMemberRoleAssignment.assignmentTimestamp">Assignment Timestamp</Translate>
            </span>
          </dt>
          <dd>
            {projectMemberRoleAssignmentEntity.assignmentTimestamp ? (
              <TextFormat value={projectMemberRoleAssignmentEntity.assignmentTimestamp} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="freecountApp.projectMemberRoleAssignment.projectMember">Project Member</Translate>
          </dt>
          <dd>{projectMemberRoleAssignmentEntity.projectMember ? projectMemberRoleAssignmentEntity.projectMember.id : ''}</dd>
          <dt>
            <Translate contentKey="freecountApp.projectMemberRoleAssignment.projectMemberRole">Project Member Role</Translate>
          </dt>
          <dd>
            {projectMemberRoleAssignmentEntity.projectMemberRoles
              ? projectMemberRoleAssignmentEntity.projectMemberRoles.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {projectMemberRoleAssignmentEntity.projectMemberRoles &&
                    i === projectMemberRoleAssignmentEntity.projectMemberRoles.length - 1
                      ? ''
                      : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/project-member-role-assignment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/project-member-role-assignment/${projectMemberRoleAssignmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProjectMemberRoleAssignmentDetail;
