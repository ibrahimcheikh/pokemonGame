import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
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
    return this.http.get<Pokemon[]>(`${this.api}/pokemons`);
  }

  public createNewPayer(player: Player): Observable<Player> {
    return this.http.post<Player>(`${this.api}/players/add`, player);
  }

  public startGame(player: Player[]): Observable<Battle> {
    return this.http.post<Battle>(`${this.api}/start`, player);
  }
}
