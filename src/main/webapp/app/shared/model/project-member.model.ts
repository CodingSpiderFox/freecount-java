import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IProject } from 'app/shared/model/project.model';
import { ProjectPermission } from 'app/shared/model/enumerations/project-permission.model';
import { ProjectMemberRole } from 'app/shared/model/enumerations/project-member-role.model';

export interface IProjectMember {
  id?: number;
  additionalProjectPermissions?: ProjectPermission;
  roleInProject?: ProjectMemberRole;
  addedTimestamp?: string;
  user?: IUser | null;
  project?: IProject;
}

export const defaultValue: Readonly<IProjectMember> = {};
