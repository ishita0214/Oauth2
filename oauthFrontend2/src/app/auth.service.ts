import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'; // Import HttpClient
import { Observable } from 'rxjs';
import { User } from './User';

@Injectable({
  providedIn: 'root' // This ensures the service is available throughout the app
})
export class AuthService {
  private baseUrl = 'http://localhost:8080'; // Your backend URL

  constructor(private http: HttpClient) {} // Inject HttpClient

  loginWithGoogle() {
    window.location.href = `${this.baseUrl}/authorization/google`;
  }

  handleLoginCallback(code: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/login?code=${code}`); // Use HttpClient to make requests
  }
  findUserByEmail(email: string): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/findByEmail/${email}`);
  }
 
}