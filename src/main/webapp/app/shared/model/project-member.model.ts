import { IUser } from 'app/shared/model/user.model';
import { IProject } from 'app/shared/model/project.model';
import { ProjectPermission } from 'app/shared/model/enumerations/project-permission.model';

export interface IProjectMember {
  id?: number;
  additionalProjectPermissions?: ProjectPermission;
  user?: IUser | null;
  project?: IProject;
}

export const defaultValue: Readonly<IProjectMember> = {};
