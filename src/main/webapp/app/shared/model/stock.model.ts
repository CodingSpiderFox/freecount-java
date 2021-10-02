import dayjs from 'dayjs';
import { IProduct } from 'app/shared/model/product.model';

export interface IStock {
  id?: number;
  addedTimestamp?: string;
  storageLocation?: string | null;
  calculatedExpiryTimestamp?: string;
  manualSetExpiryTimestamp?: string | null;
  product?: IProduct;
}

export const defaultValue: Readonly<IStock> = {};
