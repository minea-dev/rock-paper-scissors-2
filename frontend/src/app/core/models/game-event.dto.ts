import { GameResponseDto } from './game-response.dto';

export type GameEventType =
  | 'CONNECTION_ESTABLISHED'
  | 'PLAYER_JOINED'
  | 'ROUND_FINISHED'
  | 'GAME_OVER'
  | 'PLAYER_LEFT';

export interface GameEventMessage {
  type: GameEventType;
  payload?: GameResponseDto;
  message?: string;
}
