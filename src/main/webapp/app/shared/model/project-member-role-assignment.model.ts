import dayjs from 'dayjs';
import { IProjectMember } from 'app/shared/model/project-member.model';
import { IProjectMemberRole } from 'app/shared/model/project-member-role.model';

export interface IProjectMemberRoleAssignment {
  id?: number;
  assignmentTimestamp?: string;
  projectMember?: IProjectMember;
  projectMemberRoles?: IProjectMemberRole[];
}

export const defaultValue: Readonly<IProjectMemberRoleAssignment> = {};
