import { Component } from '@angular/core';

 @Component({
   selector: 'app-root',
   templateUrl: './app.component.html',
   styleUrls: ['./app.component.css']
 })
 export class AppComponent {
   title = 'es4';

   ngOnInit() {
    console.log("on init funziona")
  }
   
  toggle = true;
   onStudentClick(){
    this.toggle = !this.toggle;
  }
 }

 

