import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IProjectMember } from 'app/shared/model/project-member.model';
import { getEntities as getProjectMembers } from 'app/entities/project-member/project-member.reducer';
import { IProjectMemberPermission } from 'app/shared/model/project-member-permission.model';
import { getEntities as getProjectMemberPermissions } from 'app/entities/project-member-permission/project-member-permission.reducer';
import { getEntity, updateEntity, createEntity, reset } from './project-member-permission-assignment.reducer';
import { IProjectMemberPermissionAssignment } from 'app/shared/model/project-member-permission-assignment.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProjectMemberPermissionAssignmentUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const projectMembers = useAppSelector(state => state.projectMember.entities);
  const projectMemberPermissions = useAppSelector(state => state.projectMemberPermission.entities);
  const projectMemberPermissionAssignmentEntity = useAppSelector(state => state.projectMemberPermissionAssignment.entity);
  const loading = useAppSelector(state => state.projectMemberPermissionAssignment.loading);
  const updating = useAppSelector(state => state.projectMemberPermissionAssignment.updating);
  const updateSuccess = useAppSelector(state => state.projectMemberPermissionAssignment.updateSuccess);

  const handleClose = () => {
    props.history.push('/project-member-permission-assignment');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getProjectMembers({}));
    dispatch(getProjectMemberPermissions({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.assignmentTimestamp = convertDateTimeToServer(values.assignmentTimestamp);

    const entity = {
      ...projectMemberPermissionAssignmentEntity,
      ...values,
      projectMemberPermissions: mapIdList(values.projectMemberPermissions),
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
          ...projectMemberPermissionAssignmentEntity,
          assignmentTimestamp: convertDateTimeFromServer(projectMemberPermissionAssignmentEntity.assignmentTimestamp),
          projectMemberId: projectMemberPermissionAssignmentEntity?.projectMember?.id,
          projectMemberPermissions: projectMemberPermissionAssignmentEntity?.projectMemberPermissions?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2
            id="freecountApp.projectMemberPermissionAssignment.home.createOrEditLabel"
            data-cy="ProjectMemberPermissionAssignmentCreateUpdateHeading"
          >
            <Translate contentKey="freecountApp.projectMemberPermissionAssignment.home.createOrEditLabel">
              Create or edit a ProjectMemberPermissionAssignment
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
                  id="project-member-permission-assignment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('freecountApp.projectMemberPermissionAssignment.assignmentTimestamp')}
                id="project-member-permission-assignment-assignmentTimestamp"
                name="assignmentTimestamp"
                data-cy="assignmentTimestamp"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="project-member-permission-assignment-projectMember"
                name="projectMemberId"
                data-cy="projectMember"
                label={translate('freecountApp.projectMemberPermissionAssignment.projectMember')}
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
                label={translate('freecountApp.projectMemberPermissionAssignment.projectMemberPermission')}
                id="project-member-permission-assignment-projectMemberPermission"
                data-cy="projectMemberPermission"
                type="select"
                multiple
                name="projectMemberPermissions"
              >
                <option value="" key="0" />
                {projectMemberPermissions
                  ? projectMemberPermissions.map(otherEntity => (
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
                to="/project-member-permission-assignment"
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

export default ProjectMemberPermissionAssignmentUpdate;
