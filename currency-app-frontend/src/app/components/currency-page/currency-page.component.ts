import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CurrencyHistoryComponent } from './currency-history/currency-history.component';
import { CurrencyCalculatorComponent } from './currency-calculator/currency-calculator.component';
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
  imports: [CurrencyHistoryComponent, CurrencyCalculatorComponent],
  templateUrl: './currency-page.component.html',
})
export class CurrencyComponent implements OnInit {
  currency: string = '';

  constructor(private route: ActivatedRoute) {}
  /**
   * ngOnInit lifecycle hook which runs when the component is initialized.
   * It fetches the currency parameter from the URL using ActivatedRoute and stores it in the currency property.
   */
  ngOnInit(): void {
    this.currency = this.route.snapshot.paramMap.get('currency') || '';
  }
}
