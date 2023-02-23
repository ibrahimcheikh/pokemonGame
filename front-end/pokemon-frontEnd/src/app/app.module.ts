import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { NgSelectOption, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { PlayerformComponent } from './playerform/playerform.component';

@NgModule({
  declarations: [
    AppComponent,
    PlayerformComponent,
    HeaderComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    ReactiveFormsModule,
    NgSelectModule,
    RouterModule.forRoot([
      { path: '', component: PlayerformComponent },
    ])
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
