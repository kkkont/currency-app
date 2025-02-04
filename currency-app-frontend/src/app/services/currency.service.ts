// currency.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ExchangeRate } from '../models/exchange-rate.model';
import { HttpParams } from '@angular/common/http';
@Injectable({
  providedIn: 'root',
})
export class CurrencyService {
  private apiUrl = 'http://localhost:8080/currency-app'; // Backend URL

  constructor(private http: HttpClient) {}

  // Fetch all currencies with their latest exchange rates
  getLatestExchangeRates(): Observable<any> {
    return this.http.get<ExchangeRate[]>(
      `${this.apiUrl}/exchange-rates/latest`
    );
  }

  // Fetch historical exchange rates for a specific currency
  getCurrencyHistory(currency: string): Observable<any> {
    const params = new HttpParams().set('currency', currency);

    return this.http.get<ExchangeRate[]>(
      `${this.apiUrl}/exchange-rates/history`,
      {
        params,
      }
    );
  }

  // Calculate equivalent amount in target currency
  calculateCurrency(currency: string, amountInEur: number): Observable<any> {
    const params = new HttpParams()
      .set('currency', currency)
      .set('euro', amountInEur.toString());

    return this.http.get<number>(`${this.apiUrl}/exchange-rates/currencycalc`, {
      params,
    });
  }
}
