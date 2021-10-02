import dayjs from 'dayjs';
import { IProject } from 'app/shared/model/project.model';

export interface IBill {
  id?: number;
  title?: string;
  closedTimestamp?: string | null;
  finalAmount?: number | null;
  project?: IProject;
}

export const defaultValue: Readonly<IBill> = {};
