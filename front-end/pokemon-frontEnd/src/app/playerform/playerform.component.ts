import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, SecurityContext } from '@angular/core';
import { UntypedFormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
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
  playerNumber: number = 0;

  playerForm = this.fb.group({
    playerName: ['', Validators.required],
    pokemonName: ['', Validators.required],
  });
  constructor(private pokemonService: PokemonService, private fb: UntypedFormBuilder, private sanitizer: DomSanitizer) {
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
    console.log("this.playerList.length==2 ",this.playerList.length==2)
    if(this.playerList.length==2){
      this.pokemonService.startGame(this.playerList).subscribe(
        (battle: Battle) => {
          this.battle = battle
          this.battle.winner == null ? alert("It was a tie no one won!") : alert(this.battle.winner.name)
        },
        (error: HttpErrorResponse) => {
          alert(error.message)
        }
      )
      this.playerList=[];
      console.log("in start method playerlist is ",this.playerList)
    }
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
    console.log("this.playerForm.valid",this.playerForm.valid)
    if (this.playerForm.valid){
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
        console.log("in onSubmit method playerlist is ",this.playerList)
    }
 

  }
}
