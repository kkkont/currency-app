export interface ExchangeRate {
  id: number;
  currency: string;
  currencyDenom: string;
  obsValue: number;
  title: string;
  titleCompl: string;
  timestamp: Date;
}
