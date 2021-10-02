import dayjs from 'dayjs';
import { ProjectMemberRoleEnum } from 'app/shared/model/enumerations/project-member-role-enum.model';

export interface IProjectMemberRole {
  id?: number;
  createdTimestamp?: string;
  projectMemberRole?: ProjectMemberRoleEnum;
}

export const defaultValue: Readonly<IProjectMemberRole> = {};
