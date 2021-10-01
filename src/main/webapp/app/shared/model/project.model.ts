import dayjs from 'dayjs';

export interface IProject {
  id?: number;
  name?: string;
  key?: string;
  createTimestamp?: string;
}

export const defaultValue: Readonly<IProject> = {};
