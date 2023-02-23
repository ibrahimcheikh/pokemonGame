package com.pokemon.ibrahim.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pokemon.ibrahim.model.Battle;
import com.pokemon.ibrahim.model.Player;
import com.pokemon.ibrahim.model.Pokemon;
import com.pokemon.ibrahim.service.PokemonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PokemonController {
    private final PokemonService pokemonService;

    @GetMapping("/pokemons")
    public ResponseEntity<List<Pokemon>> getAllPokemons() throws JsonProcessingException {
        List<Pokemon> pokemons = pokemonService.getAllPokemons();
        if (pokemons == null) {
            pokemons = new ArrayList<>();
        }
        return new ResponseEntity<>(pokemons, HttpStatus.OK);
    }

    @PostMapping("/players/add")
    public ResponseEntity<Player> createNewPayer (@RequestBody Player player){
        return ResponseEntity.status(HttpStatus.CREATED).body(player);
    }

    @PostMapping("/start")
    public ResponseEntity<Battle> startGame(@RequestBody List<Player> players,Battle battle) {
        battle = pokemonService.start(players);
        return ResponseEntity.ok(battle);
    }
}
