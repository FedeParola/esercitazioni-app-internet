import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment'

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {

  constructor(private http: HttpClient) { }

  getLines() {
    return this.http.get(environment.apiUrl+'/lines');
  }

  getReservations(lineName: string, date: Date) {
    return this.http.get(environment.apiUrl+'/reservations/'+lineName+"/"+date.toISOString().substring(0, 10));
  }
}
