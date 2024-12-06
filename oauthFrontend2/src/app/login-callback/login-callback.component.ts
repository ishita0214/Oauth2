import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { CommonModule } from '@angular/common';
import { User } from '../User';

@Component({
  selector: 'app-login-callback',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './login-callback.component.html',
  styleUrl: './login-callback.component.css'
})
export class LoginCallbackComponent implements OnInit {

  constructor(private route: ActivatedRoute, private authService: AuthService, private router: Router) {}
  User!:User
  ngOnInit() {
    
    const code = this.route.snapshot.queryParamMap.get('code');
    console.log(code);
    
    if (code) {

      this.authService.handleLoginCallback(code).subscribe({
        next: (response) => {

          console.log('Login Successful:', response);
          localStorage.setItem('token', response.accessToken); // Save JWT
          this.authService.findUserByEmail(response.email).subscribe((data)=>{
            this.User=data;
            console.log(this.User);
            

          })
        },
        error: (err) => {
          console.error('Login Failed:', err);
          //this.router.navigate(['/login']); // Redirect to login
        }
      });
    } else {
      console.log('Authorization code missing');
      //this.router.navigate(['/login']); // Redirect to login
    }
  }
  places= [
    {
      name: 'Bara Imambara',
      image: 'assets/images/baraImambara.jpg',
      description: 'Bara Imambara is a historical monument and a major tourist attraction in Lucknow, known for its grand architecture and the Bhool Bhulaiya labyrinth.'
    },
    {
      name: 'Chota Imambara',
      image: 'assets/images/chotaImambara.jpg', 
      description: 'Chota Imambara is a beautiful mausoleum in Lucknow, also known as the Hussainabad Imambara. It is adorned with chandeliers and intricate decorations.'
    },
    {
      name: 'Rumi Darwaza',
      image: 'assets/images/rumiDarwaza.jpg', 
      description: 'Rumi Darwaza is a majestic gateway in Lucknow, famous for its stunning Awadhi architecture and historical significance.'
    },
    {
      name: 'Ambedkar Memorial Park',
      image: 'assets/images/ambedkar.jpg', 
      description: 'Ambedkar Memorial Park is a sprawling park dedicated to Dr. B.R. Ambedkar, featuring statues, gardens, and a museum that reflects the history of India.'
    }
  ];
  
}