import { Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { LoginCallbackComponent } from './login-callback/login-callback.component';
import { LoginComponent } from './login/login.component';

export const routes: Routes = [
    {
        path: 'login',
        component: LoginComponent
    },
    {
        path: 'callback',
        component: LoginCallbackComponent
    }
  
];
