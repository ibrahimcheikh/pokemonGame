import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, SecurityContext } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Battle } from '../Battle';
import { Player } from '../Player';
import { Pokemon } from '../Pokemon';
import { PokemonService } from '../pokemon.service';
@Component({
  selector: 'app-playerform',
  templateUrl: './playerform.component.html',
  styleUrls: ['./playerform.component.css']
})
export class PlayerformComponent implements OnInit {
  pokemonList: Pokemon[] = [];
  playerList: Player[] = [];
  battle!: Battle;
  pokemonUrl: string | null;
  player!: Player;
  playerNumber: number = 1;

  playerForm = this.fb.group({
    playerName: ['', Validators.required],
    pokemonName: ['', Validators.required],
  });
  constructor(private pokemonService: PokemonService, private fb: FormBuilder, private sanitizer: DomSanitizer) {
    this.pokemonUrl = "";
  }

  ngOnInit(): void {
    this.getPokemons();

  }

  public getPokemons() {
    this.pokemonService.getAllPokemons().subscribe(
      (pokemonList: Pokemon[]) => {
        this.pokemonList = pokemonList;
        console.log(this.pokemonList)
      },
      (error: HttpErrorResponse) => {
        alert(error.message)
      }
    )
  }

  public start(){
    this.pokemonService.startGame(this.playerList).subscribe(
      (battle: Battle) => {
        this.battle = battle

      },
      (error: HttpErrorResponse) => {
        alert(error.message)
      }
    )
  }

  public img(): string {
    const name = this.playerForm.get('pokemonName')?.value;
    const pokemon = this.pokemonList.find(pokemon => pokemon.name === name) as Pokemon
    console.log(pokemon?.url)
    return pokemon?.url
  }
  public getSafeImageUrl() {
    const url = this.img();
    this.pokemonUrl = this.sanitizer.sanitize(SecurityContext.RESOURCE_URL, this.sanitizer.bypassSecurityTrustResourceUrl(url))
  }
  findPokemonByName(name: string): Pokemon | undefined {
    return this.pokemonList.find(pokemon => pokemon.name === name);
  }
  onSubmit() {

    const playerName = this.playerForm.get('playerName')?.value
    const pokemonName = this.playerForm.get('pokemonName')?.value;
    let player: Player = {
      name: playerName,
      pokemon: this.pokemonList.find(pokemon => pokemon.name === pokemonName) as Pokemon
    }
      this.pokemonService.createNewPayer(player).subscribe(
        (player: Player) => {
          this.player = player
          this.playerList.push(this.player);
          this.playerNumber++;
          this.playerForm.reset();
        },
        (error: HttpErrorResponse) => {
          alert(error.message)
          this.playerForm.reset()
        }
      )

  }
}
