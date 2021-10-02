import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IProjectMember } from 'app/shared/model/project-member.model';
import { getEntities as getProjectMembers } from 'app/entities/project-member/project-member.reducer';
import { IProjectMemberRole } from 'app/shared/model/project-member-role.model';
import { getEntities as getProjectMemberRoles } from 'app/entities/project-member-role/project-member-role.reducer';
import { getEntity, updateEntity, createEntity, reset } from './project-member-role-assignment.reducer';
import { IProjectMemberRoleAssignment } from 'app/shared/model/project-member-role-assignment.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProjectMemberRoleAssignmentUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const projectMembers = useAppSelector(state => state.projectMember.entities);
  const projectMemberRoles = useAppSelector(state => state.projectMemberRole.entities);
  const projectMemberRoleAssignmentEntity = useAppSelector(state => state.projectMemberRoleAssignment.entity);
  const loading = useAppSelector(state => state.projectMemberRoleAssignment.loading);
  const updating = useAppSelector(state => state.projectMemberRoleAssignment.updating);
  const updateSuccess = useAppSelector(state => state.projectMemberRoleAssignment.updateSuccess);

  const handleClose = () => {
    props.history.push('/project-member-role-assignment');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getProjectMembers({}));
    dispatch(getProjectMemberRoles({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.assignmentTimestamp = convertDateTimeToServer(values.assignmentTimestamp);

    const entity = {
      ...projectMemberRoleAssignmentEntity,
      ...values,
      projectMemberRoles: mapIdList(values.projectMemberRoles),
      projectMember: projectMembers.find(it => it.id.toString() === values.projectMemberId.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          assignmentTimestamp: displayDefaultDateTime(),
        }
      : {
          ...projectMemberRoleAssignmentEntity,
          assignmentTimestamp: convertDateTimeFromServer(projectMemberRoleAssignmentEntity.assignmentTimestamp),
          projectMemberId: projectMemberRoleAssignmentEntity?.projectMember?.id,
          projectMemberRoles: projectMemberRoleAssignmentEntity?.projectMemberRoles?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="freecountApp.projectMemberRoleAssignment.home.createOrEditLabel" data-cy="ProjectMemberRoleAssignmentCreateUpdateHeading">
            <Translate contentKey="freecountApp.projectMemberRoleAssignment.home.createOrEditLabel">
              Create or edit a ProjectMemberRoleAssignment
            </Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="project-member-role-assignment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('freecountApp.projectMemberRoleAssignment.assignmentTimestamp')}
                id="project-member-role-assignment-assignmentTimestamp"
                name="assignmentTimestamp"
                data-cy="assignmentTimestamp"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="project-member-role-assignment-projectMember"
                name="projectMemberId"
                data-cy="projectMember"
                label={translate('freecountApp.projectMemberRoleAssignment.projectMember')}
                type="select"
                required
              >
                <option value="" key="0" />
                {projectMembers
                  ? projectMembers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                label={translate('freecountApp.projectMemberRoleAssignment.projectMemberRole')}
                id="project-member-role-assignment-projectMemberRole"
                data-cy="projectMemberRole"
                type="select"
                multiple
                name="projectMemberRoles"
              >
                <option value="" key="0" />
                {projectMemberRoles
                  ? projectMemberRoles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button
                tag={Link}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/project-member-role-assignment"
                replace
                color="info"
              >
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ProjectMemberRoleAssignmentUpdate;
