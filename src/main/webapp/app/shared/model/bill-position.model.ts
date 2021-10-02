import { IBill } from 'app/shared/model/bill.model';
import { IProduct } from 'app/shared/model/product.model';

export interface IBillPosition {
  id?: number;
  title?: string;
  cost?: number;
  order?: number;
  bill?: IBill;
  product?: IProduct | null;
}

export const defaultValue: Readonly<IBillPosition> = {};
