export interface RoundResponseDto {
  roundNumber: number;
  player1Move: string;
  player2Move: string;
  result: 'WIN_PLAYER_1' | 'WIN_PLAYER_2' | 'DRAW';
}
