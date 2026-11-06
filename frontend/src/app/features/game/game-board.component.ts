export interface CreateGameCommand {
  hostId: string;
  hostUsername: string;
  gameMode: 'CLASSIC' | 'EXTENDED';
  bestOfRounds: number;
  vsCPU: boolean;
}

export interface JoinGameCommand {
  gameId: string;
  guestId: string;
  guestUsername: string;
}

export interface PlayTurnCommand {
  gameId: string;
  playerId: string;
  move: 'ROCK' | 'PAPER' | 'SCISSORS' | 'LIZARD' | 'SPOCK';
}
