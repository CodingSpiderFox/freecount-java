import dayjs from 'dayjs';
import { ProjectMemberPermissionEnum } from 'app/shared/model/enumerations/project-member-permission-enum.model';

export interface IProjectMemberPermission {
  id?: number;
  createdTimestamp?: string;
  projectMemberPermission?: ProjectMemberPermissionEnum;
}

export const defaultValue: Readonly<IProjectMemberPermission> = {};
