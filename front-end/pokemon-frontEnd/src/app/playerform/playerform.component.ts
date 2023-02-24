import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, SecurityContext } from '@angular/core';
import { UntypedFormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
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
  battleFieldLogs: string = "";
  isEditMode: boolean = false;
  //To keep track of players
  playerIndex: number = 0;
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
  //Get all Players
  public getAllPlayers() {
    this.pokemonService.getAllPlayers().subscribe(
      (players: Player[]) => {
        this.playerList = players
      },
      (error: HttpErrorResponse) => {
        alert(error.message)
        this.playerForm.reset()
      }
    )
  }

  //Create a player
  private createPlayer(name: string, pokemonName: string): Player {
    return {
      name: name,
      pokemon: this.pokemonList.find(pokemon => pokemon.name === pokemonName) as Pokemon
    };
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
              //Call start again to play again
              this.isEditMode = false;
              this.start();
            }
            else if (result === 'change-pokemon') {
              // Call on submit with isEditMode ==true
              this.isEditMode = true;
              this.onSubmit();
            }
          });
        },
        (error: HttpErrorResponse) => {
          alert(error.message)
        }
      );
    }
  }


  onSubmit() {
    const playerName = this.playerForm.get('playerName')?.value;
    const pokemonName = this.playerForm.get('pokemonName')?.value;
    //if we didnt press restart
    // If we're not in edit mode, create a new player
    if (this.playerForm.valid && !this.isEditMode) {
      const player = this.createPlayer(playerName, pokemonName);
      this.pokemonService.createNewPayer(player).subscribe(
        (createdPlayer: Player) => {
          this.playerList.push(createdPlayer);
          this.playerForm.reset();
        },
        (error: HttpErrorResponse) => {
          alert(error.message);
          this.playerForm.reset();
        }
      );
    }

    if (this.isEditMode) {
      let playerName = this.playerList[this.playerIndex].name;
      // Setting the player's name and disabling it
      this.playerForm.get('playerName')?.setValue(playerName);
      this.playerForm.controls['playerName'].disable();
      let pokemonName = this.playerForm.get('pokemonName')?.value;
      // We need to check if the player has chosen a new pokemon
      if (pokemonName) {
        const player = this.createPlayer(playerName, pokemonName);
        this.pokemonService.updatePlayer(player.name, player).subscribe(
          (updatedPlayer: Player) => {
            this.player = updatedPlayer;
            this.playerIndex = (this.playerIndex + 1) % this.playerList.length;

          },
          (error: HttpErrorResponse) => {
            alert(error.message);
            this.playerForm.reset();
          }
        );
      }
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
