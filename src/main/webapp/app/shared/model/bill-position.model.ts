import { IBill } from 'app/shared/model/bill.model';

export interface IBillPosition {
  id?: number;
  title?: string;
  cost?: number;
  order?: number;
  bill?: IBill;
}

export const defaultValue: Readonly<IBillPosition> = {};
