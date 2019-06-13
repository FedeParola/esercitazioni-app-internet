import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, EmailValidator } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from '../authentication.service';
import { MatSnackBar } from '@angular/material';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  form: FormGroup;

  constructor(private _snackBar: MatSnackBar,
    private fb: FormBuilder, 
    private authService: AuthenticationService, 
    private router: Router) {
      this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.pattern('^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{6,32}$')]],
      confPassword: ['', Validators.required]
    },{
      validators: [this.matchingConfirmPasswords, this.checkEmail]
    });
  }

  register() {
    const val = this.form.value;

    if (val.email && val.password && val.confPassword) {
      this.authService.register(val.email, val.password, val.confPassword)
          .subscribe(
              () => {
                console.log("User is registered");
                this.router.navigateByUrl('/login');
              },
              () => {
                this._snackBar.open("Something went wrong", "",
                    { panelClass: 'register-error-snackbar', duration: 5000 });
              }
          );
    }
  }

  checkEmail(form: FormGroup){
    const val = form.value;
    console.log("entrato nel validator");
    if(val.email){
      this.authService.checkEmail(val.email).
        subscribe(
          () => {
            console.log("email già usata");
            return form.controls['email'].setErrors({ alreadyUsed: true });
          },
          () => {
            console.log("errore getUsers");
            return null;
          }
        )
    }
  }

  matchingConfirmPasswords(form: FormGroup) { 
    const val = form.value;
    if (val.password === val.confPassword) { 
      return null; 
    } 
    else { 
        return form.controls['confPassword'].setErrors({ passwordNotEquivalent: true }); 
    } 
  }

  ngOnInit() {
  }

}

