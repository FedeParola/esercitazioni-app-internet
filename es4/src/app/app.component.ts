import { Component } from '@angular/core';
import {PageEvent} from '@angular/material';
import {RoutesService} from './routes.service';


 @Component({
   selector: 'app-root',
   templateUrl: './app.component.html',
   styleUrls: ['./app.component.css']
 })
 export class AppComponent {
   title = 'es4';
   routes: Object[];

   pageCount: number;
   pageSize: number;
   pageEvent: PageEvent;

  constructor(private routesService: RoutesService){}

   ngOnInit() {
    this.routes=this.routesService.getRoutes();
    this.pageCount=this.routes.length;
    this.pageSize=1;
  }
   
   onChildClick(child){
     child.present ? child.present = false : child.present = true;
   }

 }

 

