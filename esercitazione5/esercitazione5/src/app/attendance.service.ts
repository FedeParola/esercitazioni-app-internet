import { Injectable } from '@angular/core';
import { routes } from './data';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment'

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {

  constructor(private http: HttpClient) { }

  getLines(): Observable<Object> {
    return this.http.get(environment.apiUrl+'/lines');
  }

  getReservations(lineName: string, date: Date) {
    return this.http.get(environment.apiUrl+'/reservations/'+lineName+"/"+date.toISOString().substring(0, 10));
  }

  getRoutes() {
    return routes;
  }
}
