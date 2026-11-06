import { RoundResponseDto } from './round-response.dto';

export interface PlayerDto {
  id: string;
  username: string;
}

export interface GameResponseDto {
  id: string;
  status: 'WAITING_FOR_PLAYER' | 'IN_PROGRESS' | 'FINISHED' | 'ABANDONED';
  player1: PlayerDto;
  player2: PlayerDto | null;
  scorePlayer1: number;
  scorePlayer2: number;
  hasUserMoved: boolean;
  hasOpponentMoved: boolean;
  history: RoundResponseDto[];
  winnerId: string | null;
}
