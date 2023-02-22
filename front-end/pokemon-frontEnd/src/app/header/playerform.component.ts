import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Pokemon } from '../Pokemon';
import { PokemonService } from '../pokemon.service';
@Component({
  selector: 'app-playerform',
  templateUrl: './playerform.component.html',
  styleUrls: ['./playerform.component.css']
})
export class PlayerformComponent implements OnInit {
  pokemonList:Pokemon[] =[];
  playerForm: FormGroup;

  constructor(private fb: FormBuilder,private pokemonService:PokemonService) { 
    this.playerForm = this.fb.group({
      playerName: ['', Validators.required],
      chosenPokemon: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.getPokemons();
 
  }

  public getPokemons(){
    this.pokemonService.getAllPokemons().subscribe(
      (pokemonList:Pokemon[])=>{
        this.pokemonList = pokemonList;
        console.log(this.pokemonList)
      },
      (error:HttpErrorResponse)=>{
        alert(error.message)
      }
    )
  }

  onSubmit() {
    console.log(this.playerForm.value);
  }

}
