import dayjs from 'dayjs';
import { IFinanceAccount } from 'app/shared/model/finance-account.model';

export interface IFinanceTransactions {
  id?: string;
  executionTimestamp?: string;
  amountAddedToDestinationAccount?: number;
  comment?: string | null;
  destinationAccount?: IFinanceAccount;
  referenceAccount?: IFinanceAccount;
}

export const defaultValue: Readonly<IFinanceTransactions> = {};
