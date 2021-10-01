import { IProject } from 'app/shared/model/project.model';

export interface IBill {
  id?: number;
  title?: string;
  project?: IProject;
}

export const defaultValue: Readonly<IBill> = {};
