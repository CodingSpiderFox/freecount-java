import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity, updateEntity, createEntity, reset } from './project-member-role.reducer';
import { IProjectMemberRole } from 'app/shared/model/project-member-role.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProjectMemberRoleUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const projectMemberRoleEntity = useAppSelector(state => state.projectMemberRole.entity);
  const loading = useAppSelector(state => state.projectMemberRole.loading);
  const updating = useAppSelector(state => state.projectMemberRole.updating);
  const updateSuccess = useAppSelector(state => state.projectMemberRole.updateSuccess);

  const handleClose = () => {
    props.history.push('/project-member-role');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(props.match.params.id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.createdTimestamp = convertDateTimeToServer(values.createdTimestamp);

    const entity = {
      ...projectMemberRoleEntity,
      ...values,
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
          createdTimestamp: displayDefaultDateTime(),
        }
      : {
          projectMemberRole: 'Y',
          ...projectMemberRoleEntity,
          createdTimestamp: convertDateTimeFromServer(projectMemberRoleEntity.createdTimestamp),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="freecountApp.projectMemberRole.home.createOrEditLabel" data-cy="ProjectMemberRoleCreateUpdateHeading">
            <Translate contentKey="freecountApp.projectMemberRole.home.createOrEditLabel">Create or edit a ProjectMemberRole</Translate>
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
                  id="project-member-role-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('freecountApp.projectMemberRole.createdTimestamp')}
                id="project-member-role-createdTimestamp"
                name="createdTimestamp"
                data-cy="createdTimestamp"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('freecountApp.projectMemberRole.projectMemberRole')}
                id="project-member-role-projectMemberRole"
                name="projectMemberRole"
                data-cy="projectMemberRole"
                type="select"
              >
                <option value="Y">{translate('freecountApp.ProjectMemberRoleEnum.Y')}</option>
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/project-member-role" replace color="info">
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

export default ProjectMemberRoleUpdate;
