import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Notification {
  id: number;
  title: string;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private baseUrl = '/api/notifications';

  constructor(private http: HttpClient) {}

  getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(this.baseUrl);
  }

  getUnreadCount(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/unread-count`);
  }

  markAllAsRead(): Observable<any> {
    return this.http.patch(`${this.baseUrl}/mark-all-read`, {});
  }

  markAsRead(id: number): Observable<any> {
    return this.http.patch(`${this.baseUrl}/${id}/read`, {});
  }
}