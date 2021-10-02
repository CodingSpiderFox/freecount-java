import dayjs from 'dayjs';
import { IProjectMember } from 'app/shared/model/project-member.model';
import { IProjectMemberPermission } from 'app/shared/model/project-member-permission.model';

export interface IProjectMemberPermissionAssignment {
  id?: number;
  assignmentTimestamp?: string;
  projectMember?: IProjectMember;
  projectMemberPermissions?: IProjectMemberPermission[];
}

export const defaultValue: Readonly<IProjectMemberPermissionAssignment> = {};
