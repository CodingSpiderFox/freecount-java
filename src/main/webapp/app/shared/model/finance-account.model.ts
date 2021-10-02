import { IUser } from 'app/shared/model/user.model';

export interface IFinanceAccount {
  id?: string;
  title?: string;
  owner?: IUser;
}

export const defaultValue: Readonly<IFinanceAccount> = {};
