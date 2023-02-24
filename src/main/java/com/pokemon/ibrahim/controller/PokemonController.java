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

    @GetMapping("/players/all")
    public ResponseEntity<List<Player>> getAllPlayers()  {
        List<Player> players = pokemonService.getAllPlayers();
        if (players == null) {
            players = new ArrayList<>();
        }
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @PostMapping("/players/add")
    public ResponseEntity<Player> createNewPlayer(@RequestBody Player player){
        Player createdPlayer = pokemonService.createNewPlayer(player);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer);
    }

    @PostMapping("/start")
    public ResponseEntity<Battle> startGame(@RequestBody List<Player> players) {
        Battle battle = pokemonService.start(players);
        return ResponseEntity.ok(battle);
    }
    @PutMapping("/players/edit/{name}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("name") String name, @RequestBody Player player) {
        Player updatedPlayer = pokemonService.updatePlayer(name, player);
        if (updatedPlayer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPlayer);
    }
}
