import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ExchangeRateListComponent } from './components/exchange-rate-list/exchange-rate-list.component';
import { CurrencyComponent } from './components/currency-page/currency-page.component';
export const routes: Routes = [
  { path: '', component: ExchangeRateListComponent },
  { path: ':currency', component: CurrencyComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
