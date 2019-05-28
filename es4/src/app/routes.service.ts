import { Injectable } from '@angular/core';
import {routes} from './data';

@Injectable({
  providedIn: 'root'
})
export class RoutesService {

  constructor() { }

  getRoutes(){
    return routes;
  }

}
