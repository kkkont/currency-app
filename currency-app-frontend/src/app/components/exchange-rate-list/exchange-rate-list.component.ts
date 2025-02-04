import { Component, OnInit } from '@angular/core';
import { CurrencyService } from '../../services/currency.service';
import { Router } from '@angular/router';
import { NgForOf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ExchangeRate } from '../../models/exchange-rate.model';
import { CommonModule } from '@angular/common';
/**
 * ExchangeRateListComponent fetches and displays the lates exchange rates for different currencies.
 * It allows the user to click on currency to navigate to its detailed history and conversion page.
 *
 * The component retrieves the exchange rates from a backend API using the CurrencyService.
 *
 */
@Component({
  selector: 'app-exchange-rate-list',
  standalone: true,
  imports: [NgForOf, FormsModule, CommonModule],
  templateUrl: './exchange-rate-list.component.html',
})
export class ExchangeRateListComponent implements OnInit {
  rates: ExchangeRate[] = [];
  loading: boolean = false;
  errorMessage: string | null = null;

  constructor(
    private currencyService: CurrencyService,
    private router: Router
  ) {}

  /**
   * ngOnInit lifecycle hook which is executed when the component is initialized.
   * It fetches the latest exchange rates.
   */
  ngOnInit(): void {
    this.fetchExchangeRates();
  }
  /**
   * Fetches the latest exchange rates from the CurrencyService.
   * Sets the 'loading' state to true and updates the rates once the data is received.
   */
  private fetchExchangeRates(): void {
    this.loading = true; // Set loading to true when the data is being fetched
    this.currencyService.getLatestExchangeRates().subscribe({
      next: (data: ExchangeRate[]) => {
        this.rates = data;
        this.errorMessage = null; // Reset error message on successful data fetch
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage =
          'Error fetching currencies. Please try again later.'; // Display user-friendly error message
        console.error('Error fetching currencies:', error);
      },
      complete: () => {
        this.loading = false; // Set loading to false when the data fetching is completed
      },
    });
  }
  /**
   * Navigate to the currency page for the selected currency.
   * This method is triggered when a user click on a currency.
   *
   * @param currency The currency string (e.g. USD, EUR, etc.) to navigate to the currency page
   */
  goToCurrencyHistory(currency: string) {
    this.router.navigate([currency]);
  }
}
