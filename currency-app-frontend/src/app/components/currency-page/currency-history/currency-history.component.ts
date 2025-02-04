import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CurrencyService } from '../../../services/currency.service';
import { ExchangeRate } from '../../../models/exchange-rate.model';

/**
 * CurrencyHistoryComponent displays the historical exchange rate data for a given currency.
 */
@Component({
  selector: 'app-currency-history',
  imports: [CommonModule],
  templateUrl: './currency-history.component.html',
})
export class CurrencyHistoryComponent implements OnInit {
  @Input() currency: string = '';
  history: ExchangeRate[] = [];
  loading: boolean = false;
  errorMessage: string | null = null;

  constructor(private currencyService: CurrencyService) {}

  /**
   * ngOnInit lifecycle hook. It is executed when the component is initialized
   * If a currency is passed as input, it will trigger the fetching of currency history
   */
  ngOnInit(): void {
    if (!this.currency) return;
    this.fetchCurrencyHistory();
  }

  /**
   * Fetches the historical exchange rate data for the provided currency.
   * Calls the CurrencyService to get the history, then updates the component's history property.
   */
  fetchCurrencyHistory() {
    this.loading = true; // Set loading state
    this.errorMessage = null; // Reset previous error messages

    this.currencyService.getCurrencyHistory(this.currency).subscribe({
      next: (data: ExchangeRate[]) => {
        this.history = data;
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage =
          'Error fetching currency history. Please try again later.';
        console.error('Error fetching currency history:', error);
      },
      complete: () => {
        this.loading = false;
      },
    });
  }
}
