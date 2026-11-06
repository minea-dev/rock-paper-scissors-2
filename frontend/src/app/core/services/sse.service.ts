// src/app/core/services/sse.service.ts
import { Injectable, NgZone, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GameEventMessage, GameEventType } from '../models/game-event.dto';
import { GameResponseDto } from '../models/game-response.dto';

@Injectable({
  providedIn: 'root'
})
export class SseService {
  private readonly zone = inject(NgZone);
  private readonly baseUrl = 'http://localhost:8080/api/games';

  connect(gameId: string, userId?: string): Observable<GameEventMessage> {
    return new Observable<GameEventMessage>((observer) => {
      const url = userId
        ? `${this.baseUrl}/${gameId}/events?userId=${encodeURIComponent(userId)}`
        : `${this.baseUrl}/${gameId}/events`;

      const eventSource = new EventSource(url, { withCredentials: true });

      eventSource.addEventListener('CONNECTION_ESTABLISHED', (event: MessageEvent) => {
        this.zone.run(() => {
          observer.next({
            type: 'CONNECTION_ESTABLISHED',
            message: event.data
          });
        });
      });

      const businessEvents: GameEventType[] = [
        'PLAYER_JOINED',
        'ROUND_FINISHED',
        'GAME_OVER',
        'PLAYER_LEFT'
      ];

      businessEvents.forEach((eventType) => {
        eventSource.addEventListener(eventType, (event: MessageEvent) => {
          this.zone.run(() => {
            try {
              const payload: GameResponseDto = JSON.parse(event.data);
              observer.next({
                type: eventType,
                payload
              });
            } catch (err) {
              observer.error(new Error(`Error parseando payload de ${eventType}: ${err}`));
            }
          });
        });
      });

      eventSource.onerror = (error) => {
        this.zone.run(() => {
          if (eventSource.readyState === EventSource.CLOSED) {
            observer.complete();
          } else {
            observer.error(error);
          }
        });
      };

      return () => {
        eventSource.close();
      };
    });
  }
}
