package com.pokemon.ibrahim.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pokemon.ibrahim.model.Battle;
import com.pokemon.ibrahim.model.Player;
import com.pokemon.ibrahim.model.Pokemon;
import com.pokemon.ibrahim.service.PokemonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PokemonControllerTest {
    @Mock
    private PokemonService pokemonService;
    private PokemonController pokemonController;
    private List<Pokemon> pokemons;

    @BeforeEach
    public void setup() {
        pokemonController = new PokemonController(pokemonService);
        pokemons = new ArrayList<>();
        pokemons.add(new Pokemon("Pikachu", 20, 50, 100, "https://pokeapi.co/api/v2/pokemon/25","https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png\""));
        pokemons.add(new Pokemon("Charizard", 50, 100, 200, "https://pokeapi.co/api/v2/pokemon/6","https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png\""));
    }
    @Test
    public void getAllPokemons_returnsListOfPokemons() throws JsonProcessingException {
        when(pokemonService.getAllPokemons()).thenReturn(pokemons);
        ResponseEntity<List<Pokemon>> response = pokemonController.getAllPokemons();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(pokemons);
    }

    @Test
    public void startGame_returnsBattle() {
        Player player1 = new Player("Ash", new Pokemon("Pikachu", 100,80,20,"batata.com","https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png\""),0);
        Player player2 = new Player("Ibrahim", new Pokemon("Dinozor", 69,50,20,"goo.com","https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png\""),0);
        List<Player>players = new ArrayList<>(2);
        StringBuilder sb = new StringBuilder();
        players.add(player1);
        players.add(player2);
        Battle expectedBattle = new Battle(players, null,sb);
        when(pokemonService.start(players)).thenReturn(expectedBattle);
        ResponseEntity<Battle> response = pokemonController.startGame(players);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBattle, response.getBody());
        verify(pokemonService, times(1)).start(players);
    }
    @Test
    void testUpdatePlayer() {
        Player exsistingPlayer = new Player("Ash", new Pokemon("Pikachuuuu", 32,333,20,"batata.com","https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png\""),0);
        Player updatedPlayer = new Player("Ash", new Pokemon("Charizard", 100,80,20,"batata.com","https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png\""),0);
        when(pokemonService.updatePlayer("Ash", updatedPlayer)).thenReturn(updatedPlayer);
        ResponseEntity<Player> response = pokemonController.updatePlayer("Ash", updatedPlayer);
        verify(pokemonService).updatePlayer("Ash", updatedPlayer);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedPlayer, response.getBody());
    }
    @Test
    void testUpdatePlayerNotFound() {
        Player updatedPlayer = new Player("Ash", new Pokemon("Pikachu", 100,80,20,"batata.com","https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png\""),0);
        when(pokemonService.updatePlayer("Ash", updatedPlayer)).thenReturn(null);
        ResponseEntity<Player> response = pokemonController.updatePlayer("Ash", updatedPlayer);
        verify(pokemonService).updatePlayer("Ash", updatedPlayer);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}