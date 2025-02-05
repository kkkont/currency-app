import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CurrencyHistoryComponent } from './currency-history/currency-history.component';
import { CurrencyCalculatorComponent } from './currency-calculator/currency-calculator.component';
import { CurrencyData } from '../../models/currency-data.model';
import currencyData from '../../../assets/data/currency-data.json';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ExchangeRate } from '../../models/exchange-rate.model';
import { CurrencyService } from '../../services/currency.service';
/**
 * CurrencyComponent is the main container component for displaying and interacting with one currency-related features.
 * It includes child components for displaying currency history and for performing currency conversion.
 *
 * The component receives a currency parameter from the URL, which it uses to pass to child components.
 *
 */
@Component({
  selector: 'app-currency-page',
  standalone: true,
  imports: [
    CurrencyHistoryComponent,
    CurrencyCalculatorComponent,
    FormsModule,
    CommonModule,
  ],
  templateUrl: './currency-page.component.html',
})
export class CurrencyPageComponent implements OnInit {
  currency: string = '';
  currencyRate: ExchangeRate | null = null;
  currencyData: CurrencyData | null = null;
  loading: boolean = false;
  errorMessage: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private currencyService: CurrencyService
  ) {}
  /**
   * ngOnInit lifecycle hook which runs when the component is initialized.
   * It fetches the currency parameter from the URL using ActivatedRoute and stores it in the currency property.
   */
  ngOnInit(): void {
    this.loading = true;
    this.currency = this.route.snapshot.paramMap.get('currency') || '';
    this.currencyData = this.getCurrencyData(this.currency);
    this.fetchCurrencyRate();
  }

  /**
   * Fetches the latest rate data for the provided currency.
   * Calls the CurrencyService to get the latest data.
   */
  fetchCurrencyRate() {
    this.errorMessage = null;
    this.currencyService.getLatestForCurrency(this.currency).subscribe({
      next: (data: ExchangeRate) => {
        this.currencyRate = data;
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage =
          'Error fetching currency rate. Please try again later.';
        console.error('Error fetching currency history:', error);
      },
    });
  }

  /**
   * Fetches the currency data based on the provided currency code.
   * @param code The currency code (e.g., USD, EUR)
   * @returns The currency data object or null if not found
   */
  getCurrencyData(code: string): CurrencyData | null {
    const data = currencyData;
    return data.find((c) => c.code === code) || null;
  }
  /**
   * Navigates back to home page
   */
  goBack(): void {
    this.router.navigate(['/']);
  }
}
