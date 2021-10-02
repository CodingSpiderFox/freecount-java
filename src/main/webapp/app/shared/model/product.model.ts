export interface IProduct {
  id?: number;
  title?: string;
  scannerId?: string;
  usualDurationFromBuyTillExpire?: string;
  expireMeansBad?: boolean | null;
  y?: string | null;
  h?: string | null;
}

export const defaultValue: Readonly<IProduct> = {
  expireMeansBad: false,
};
