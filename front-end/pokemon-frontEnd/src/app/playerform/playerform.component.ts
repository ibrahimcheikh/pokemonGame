import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, SecurityContext } from '@angular/core';
import { UntypedFormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Battle } from '../Battle';
import { Player } from '../Player';
import { Pokemon } from '../Pokemon';
import { PokemonService } from '../pokemon.service';
import { WinnerModalComponent } from '../winner-modal/winner-modal.component';
@Component({
  selector: 'app-playerform',
  templateUrl: './playerform.component.html',
  styleUrls: ['./playerform.component.css']
})

export class PlayerformComponent implements OnInit {
  pokemonList: Pokemon[] = [];
  selectedPokemon: Pokemon | undefined;
  playerList: Player[] = [];
  battleFieldLogs:string="";
  battle!: Battle;
  player!: Player;
  playerForm = this.fb.group({
    playerName: ['', Validators.required],
    pokemonName: ['', Validators.required],
  });
  constructor(private pokemonService: PokemonService, private fb: UntypedFormBuilder, private dialog: MatDialog) { }

  ngOnInit(): void {
    this.getPokemons();

  }
  //Get all pokemons
  public getPokemons() {
    this.pokemonService.getAllPokemons().subscribe(
      (pokemonList: Pokemon[]) => {
        this.pokemonList = pokemonList;
      },
      (error: HttpErrorResponse) => {
        alert(error.message)
      }
    )
  }
  //Start the game when both players are ready 
  public start() {
    if (this.playerList.length == 2) {
      this.pokemonService.startGame(this.playerList).subscribe(
        (battle: Battle) => {
          this.battle = battle
          this.battleFieldLogs = battle.battleNarrative;
          const dialogRef = this.dialog.open(WinnerModalComponent, {
            width: '600px',
            data: { winner: this.battle.winner, logs: this.battleFieldLogs }
          });
  
          dialogRef.afterClosed().subscribe((result) => {
            if (result === 'restart') {
              //Reset everything 
              this.start();
            }
            else if (result === 'change-pokemon') {
              // Disable the player name field, but keep the value
              this.playerForm.controls['playerName'].disable();
              // Show the Pokemon selection form
            }
          });
        },
        (error: HttpErrorResponse) => {
          alert(error.message)
        }
      );
    }
  }
//Find Pokemon by Key name
  findPokemonByName(name: string): Pokemon | undefined {
    return this.pokemonList.find(pokemon => pokemon.name === name);
  }
  //When player has entered name and his pokemon
  onSubmit() {
    const playerName = this.playerForm.get('playerName')?.value
    const pokemonName = this.playerForm.get('pokemonName')?.value;
    console.log("this.playerForm.valid", this.playerForm.valid)
    if (this.playerForm.valid) {
      let player: Player = {
        name: playerName,
        pokemon: this.pokemonList.find(pokemon => pokemon.name === pokemonName) as Pokemon
      }
      this.pokemonService.createNewPayer(player).subscribe(
        (player: Player) => {
          this.player = player
          this.playerList.push(this.player);
          this.playerForm.reset();
        },
        (error: HttpErrorResponse) => {
          alert(error.message)
          this.playerForm.reset()
        }
      )
      console.log("in onSubmit method playerlist is ", this.playerList)
    }
    //Reset selection for the next Player 
    this.selectedPokemon = undefined;

  }
  //Event listener to find 
  onSelectPokemon(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const selectedName = target.value;
    this.selectedPokemon = this.pokemonList.find(pokemon => pokemon.name === selectedName);
  }

}
