import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity, updateEntity, createEntity, reset } from './project-member-permission.reducer';
import { IProjectMemberPermission } from 'app/shared/model/project-member-permission.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProjectMemberPermissionUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const projectMemberPermissionEntity = useAppSelector(state => state.projectMemberPermission.entity);
  const loading = useAppSelector(state => state.projectMemberPermission.loading);
  const updating = useAppSelector(state => state.projectMemberPermission.updating);
  const updateSuccess = useAppSelector(state => state.projectMemberPermission.updateSuccess);

  const handleClose = () => {
    props.history.push('/project-member-permission');
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
      ...projectMemberPermissionEntity,
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
          projectMemberPermission: 'CLOSE_PROJECT',
          ...projectMemberPermissionEntity,
          createdTimestamp: convertDateTimeFromServer(projectMemberPermissionEntity.createdTimestamp),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="freecountApp.projectMemberPermission.home.createOrEditLabel" data-cy="ProjectMemberPermissionCreateUpdateHeading">
            <Translate contentKey="freecountApp.projectMemberPermission.home.createOrEditLabel">
              Create or edit a ProjectMemberPermission
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
                  id="project-member-permission-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('freecountApp.projectMemberPermission.createdTimestamp')}
                id="project-member-permission-createdTimestamp"
                name="createdTimestamp"
                data-cy="createdTimestamp"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('freecountApp.projectMemberPermission.projectMemberPermission')}
                id="project-member-permission-projectMemberPermission"
                name="projectMemberPermission"
                data-cy="projectMemberPermission"
                type="select"
              >
                <option value="CLOSE_PROJECT">{translate('freecountApp.ProjectMemberPermissionEnum.CLOSE_PROJECT')}</option>
                <option value="CLOSE_BILL">{translate('freecountApp.ProjectMemberPermissionEnum.CLOSE_BILL')}</option>
                <option value="ADD_MEMBER">{translate('freecountApp.ProjectMemberPermissionEnum.ADD_MEMBER')}</option>
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/project-member-permission" replace color="info">
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

export default ProjectMemberPermissionUpdate;
