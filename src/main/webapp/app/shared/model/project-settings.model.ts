import { IProject } from 'app/shared/model/project.model';

export interface IProjectSettings {
  id?: number;
  mustProvideBillCopyByDefault?: boolean;
  project?: IProject;
}

export const defaultValue: Readonly<IProjectSettings> = {
  mustProvideBillCopyByDefault: false,
};
