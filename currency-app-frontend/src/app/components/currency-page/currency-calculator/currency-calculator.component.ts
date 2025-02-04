import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CurrencyService } from '../../../services/currency.service';
import { FormsModule } from '@angular/forms';

/**
 * CurrencyCalculatorComponent inside CurrencyPageComponent calculates the converten currency
 * amount based on the provided input amount and previously selected currency.
 */
@Component({
  selector: 'app-currency-calculator',
  imports: [CommonModule, FormsModule],
  templateUrl: './currency-calculator.component.html',
})
export class CurrencyCalculatorComponent implements OnInit {
  @Input() currency: string = '';
  amountInEur: number = 0;
  result: number | null = null;
  errorMessage: string | null = null;

  constructor(private currencyService: CurrencyService) {}

  ngOnInit(): void {}

  /**
   * This method performs the currency conversion
   * It receives the input amount as string from the input field and parses it to a float,
   * then calls the CurrencyService to perform the conversion
   *
   * @param inputAmount The amount to be converted as a string
   */
  calculateCurrency(inputAmount: string) {
    const amount = parseFloat(inputAmount);
    if (amount >= 0 && this.currency) {
      this.amountInEur = amount;
      this.currencyService
        .calculateCurrency(this.currency, this.amountInEur)
        .subscribe({
          next: (data: number) => {
            this.result = data;
          },
          error: (error) => {
            this.errorMessage =
              'Error converting currency. Please try again later.';
            console.error('Error calculating currency:', error);
          },
        });
    }
  }
}
