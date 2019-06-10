import { Injectable } from '@angular/core';
import {routes} from './data';

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {

  constructor() { }

  getRoutes(){
    return routes;
  }

}
