import { Component, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material';
import { AttendanceService } from '../attendance.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-attendance',
  templateUrl: './attendance.component.html',
  styleUrls: ['./attendance.component.css']
})
export class AttendanceComponent implements OnInit {
  title = 'esercitazione5';
  currentDate: Date;
  currentLine: number;
  reservations$: Observable<any>;
  lines: string[];

  pageCount: number;
  pageSize: number;
  pageEvent: PageEvent;

  constructor(private attendanceService: AttendanceService){}

  ngOnInit() {
    this.currentDate = new Date();
    this.currentLine = 0;
    this.attendanceService.getLines().subscribe((res: string[]) => {
      this.lines = res;
      this.loadCurrentRoute();
    },
    () => {
      console.log("Error getting lines.")
    });
    this.pageSize=1
  }

  loadCurrentRoute() {
    this.reservations$ = this.attendanceService.getReservations(this.lines[this.currentLine], this.currentDate);
  }

  onChildClick(child) {
    child.present ? child.present = false : child.present = true;
    /* Add/remove attendance from service */
  }
/*
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
  }*/

}