import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { TokenStorageService } from '../services/token-storage.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  form!: FormGroup;
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];
  redirectToHome = false;

  private subscription: Subscription = new Subscription();
  
  constructor(private authService: AuthService, private tokenStorage: TokenStorageService,
    private fb: FormBuilder, private router: Router) {}

  ngOnInit(): void {
    this.form = this.createForm()
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true
      this.roles = this.tokenStorage.getUser().roles
    }
  }

  private createForm(): FormGroup {
    return this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    this.subscription = this.authService.login(this.form.value).subscribe({
      next: (data) => {
        this.tokenStorage.saveToken(data.accessToken);
        this.tokenStorage.saveUser(data)
  
        this.isLoginFailed = false
        this.isLoggedIn = true
        this.roles = this.tokenStorage.getUser().roles
        this.redirectToHome = true;
        this.reloadPage();
        this.router.navigate(['/home']);

      },
      error: (err) => {
        this.errorMessage = err.error.message
        this.isLoginFailed = true
      }
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  reloadPage(): void {
    window.location.reload();
  }

}
