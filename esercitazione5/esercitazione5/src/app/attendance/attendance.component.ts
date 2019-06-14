import { Component, OnInit } from '@angular/core';
import { AttendanceService } from '../attendance.service';
import { Observable } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-attendance',
  templateUrl: './attendance.component.html',
  styleUrls: ['./attendance.component.css']
})
export class AttendanceComponent implements OnInit {
  currentDate: Date;
  currentLine: string;
  reservations$: Observable<any>;
  lines: string[];

  constructor(private attendanceService: AttendanceService,
              private router: Router) {}

  ngOnInit() {
    this.currentDate = new Date();
    this.attendanceService.getLines().subscribe((res: string[]) => {
      this.lines = res;
      this.currentLine = this.lines[0];
      this.loadCurrentRoute();
    },
    (error) => {
      this.handleError(error)
    });
  }

  loadCurrentRoute() {
    this.reservations$ = this.attendanceService.getReservations(this.currentLine, this.currentDate)
      .pipe(
        catchError((error) => {
          this.handleError(error);
          throw error; // Propagate error
        })
      )
  }

  onPupilClick(pupil) {
    pupil.present ? pupil.present = false : pupil.present = true;
    /* Add/remove attendance from service */
  }

  selectLine(line: string) {
    this.currentLine = line;
    this.loadCurrentRoute();
  }

  nextRoute() {
    this.currentDate = new Date(this.currentDate.valueOf()+24*60*60*1000);
    this.loadCurrentRoute();
  }

  prevRoute() {
    this.currentDate = new Date(this.currentDate.valueOf()-24*60*60*1000);
    this.loadCurrentRoute();
  }

  private handleError(error: HttpErrorResponse) {
    if (!(error.error instanceof ErrorEvent) && error.status == 401) {
      /* Not authenticated or auth expired */
      this.router.navigateByUrl('/login');
    
    } else {
      /* All other errors*/
      console.error("Error contacting server");
    }
  };
}