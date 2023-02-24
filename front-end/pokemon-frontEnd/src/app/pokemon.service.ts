import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Battle } from './Battle';
import { Player } from './Player';
import { Pokemon } from './Pokemon';

@Injectable({
  providedIn: 'root'
})
export class PokemonService {
  private api = environment.apiBaseUrl;
  constructor(private http: HttpClient) { }

  public getAllPokemons(): Observable<Pokemon[]> {
    return this.http.get<Pokemon[]>(`${this.api}/pokemons`).pipe(shareReplay({bufferSize: 1, refCount: true}));
  }

  public createNewPayer(player: Player): Observable<Player> {
    return this.http.post<Player>(`${this.api}/players/add`, player).pipe(shareReplay({bufferSize: 1, refCount: true}));
  }

  public startGame(players: Player[]): Observable<Battle> {
    return this.http.post<Battle>(`${this.api}/start`, players).pipe(shareReplay({bufferSize: 1, refCount: true}));
  }

  public updatePlayer(playerName: string, player: Player): Observable<Player> {
    const url = `${this.api}/players/edit/${playerName}`;
    return this.http.put<Player>(url, player).pipe(shareReplay({bufferSize: 1, refCount: true}));
  }

  public getAllPlayers(): Observable<Player[]> {
    return this.http.get<Player[]>(`${this.api}/players/all`).pipe(shareReplay({bufferSize: 1, refCount: true}));
  }

}
