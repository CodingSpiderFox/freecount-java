export interface IProduct {
  id?: number;
  title?: string;
  scannerId?: string;
  usualDurationFromBuyTillExpire?: string;
  expireMeansBad?: boolean | null;
  defaultPrice?: number;
}

export const defaultValue: Readonly<IProduct> = {
  expireMeansBad: false,
};
