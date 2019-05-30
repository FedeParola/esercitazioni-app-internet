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
  routes: any[];

  pageCount: number;
  pageSize: number;
  pageEvent: PageEvent;

  constructor(private routesService: RoutesService){}

  ngOnInit() {
    this.routes=this.routesService.getRoutes();
    this.pageCount=this.routes.length;
    this.pageSize=1;
  }

  onChildClick(child) {
    child.present ? child.present = false : child.present = true;
  }

  findClosestRoute() : number {
    let i;
    let current = Date.now();
    let bestDiff = Math.abs(current - this.routes[0].date.getTime());

    for (i = 1; i < this.routes.length; i++) {
      let diff = Math.abs(current - this.routes[i].date.getTime());
      if (diff > bestDiff) break;
      bestDiff = diff;
    }

    return i-1;
  }

}

 

