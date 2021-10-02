import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';
import { loadMoreDataWhenScrolled, parseHeaderForLinks } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { IQueryParams, createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IProjectMemberRoleAssignment, defaultValue } from 'app/shared/model/project-member-role-assignment.model';

const initialState: EntityState<IProjectMemberRoleAssignment> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/project-member-role-assignments';
const apiSearchUrl = 'api/_search/project-member-role-assignments';

// Actions

export const searchEntities = createAsyncThunk(
  'projectMemberRoleAssignment/search_entity',
  async ({ query, page, size, sort }: IQueryParams) => {
    const requestUrl = `${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`;
    return axios.get<IProjectMemberRoleAssignment[]>(requestUrl);
  }
);

export const getEntities = createAsyncThunk('projectMemberRoleAssignment/fetch_entity_list', async ({ page, size, sort }: IQueryParams) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}&` : '?'}cacheBuster=${new Date().getTime()}`;
  return axios.get<IProjectMemberRoleAssignment[]>(requestUrl);
});

export const getEntity = createAsyncThunk(
  'projectMemberRoleAssignment/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IProjectMemberRoleAssignment>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'projectMemberRoleAssignment/create_entity',
  async (entity: IProjectMemberRoleAssignment, thunkAPI) => {
    return axios.post<IProjectMemberRoleAssignment>(apiUrl, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'projectMemberRoleAssignment/update_entity',
  async (entity: IProjectMemberRoleAssignment, thunkAPI) => {
    return axios.put<IProjectMemberRoleAssignment>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'projectMemberRoleAssignment/partial_update_entity',
  async (entity: IProjectMemberRoleAssignment, thunkAPI) => {
    return axios.patch<IProjectMemberRoleAssignment>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'projectMemberRoleAssignment/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    return await axios.delete<IProjectMemberRoleAssignment>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

// slice

export const ProjectMemberRoleAssignmentSlice = createEntitySlice({
  name: 'projectMemberRoleAssignment',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities, searchEntities), (state, action) => {
        const links = parseHeaderForLinks(action.payload.headers.link);

        return {
          ...state,
          loading: false,
          links,
          entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links),
          totalItems: parseInt(action.payload.headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities, getEntity, searchEntities), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = ProjectMemberRoleAssignmentSlice.actions;

// Reducer
export default ProjectMemberRoleAssignmentSlice.reducer;
