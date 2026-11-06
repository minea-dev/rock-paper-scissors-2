import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { GameResponseDto } from '../models/game-response.dto';
import {CreateGameCommand, JoinGameCommand, PlayTurnCommand} from '../../features/game/game-board.component';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api/games';

  createGame(command: CreateGameCommand): Observable<GameResponseDto> {
    return this.http.post<GameResponseDto>(this.apiUrl, command);
  }

  joinGame(gameId: string, command: JoinGameCommand): Observable<GameResponseDto> {
    return this.http.post<GameResponseDto>(`${this.apiUrl}/${gameId}/join`, command);
  }

  playTurn(gameId: string, command: PlayTurnCommand): Observable<GameResponseDto> {
    return this.http.post<GameResponseDto>(`${this.apiUrl}/${gameId}/turns`, command);
  }

  getGame(gameId: string): Observable<GameResponseDto> {
    return this.http.get<GameResponseDto>(`${this.apiUrl}/${gameId}`);
  }

  abandonGame(gameId: string, playerId: string): Observable<GameResponseDto> {
    return this.http.post<GameResponseDto>(`${this.apiUrl}/${gameId}/abandon`, { playerId });
  }
}
