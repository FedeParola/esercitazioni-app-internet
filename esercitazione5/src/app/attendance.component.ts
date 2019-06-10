import { Component, OnInit } from '@angular/core';
import {PageEvent} from '@angular/material';
import {AttendanceService} from './attendance.service';

@Component({
  selector: 'app-attendance',
  templateUrl: './attendance.component.html',
  styleUrls: ['./attendance.component.css']
})
export class AttendanceComponent implements OnInit {
  title = 'esercitazione5';
  routes: any[];

  pageCount: number;
  pageSize: number;
  pageEvent: PageEvent;

  constructor(private attendanceService: AttendanceService){}

  ngOnInit() {
    this.routes=this.attendanceService.getRoutes();
    this.pageCount=this.routes.length;
    this.pageSize=1
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
