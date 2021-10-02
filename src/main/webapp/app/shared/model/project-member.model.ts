import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IProject } from 'app/shared/model/project.model';

export interface IProjectMember {
  id?: number;
  addedTimestamp?: string;
  user?: IUser | null;
  project?: IProject;
}

export const defaultValue: Readonly<IProjectMember> = {};
