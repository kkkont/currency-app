import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ExchangeRateListComponent } from './components/exchange-rate-list/exchange-rate-list.component';
import { CurrencyPageComponent } from './components/currency-page/currency-page.component';
export const routes: Routes = [
  { path: '', component: ExchangeRateListComponent },
  { path: ':currency', component: CurrencyPageComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
