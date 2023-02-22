package com.pokemon.ibrahim.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import static org.mockito.Mockito.when;

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
        pokemons.add(new Pokemon("Pikachu", 20, 50, 100, "https://pokeapi.co/api/v2/pokemon/25"));
        pokemons.add(new Pokemon("Charizard", 50, 100, 200, "https://pokeapi.co/api/v2/pokemon/6"));
    }
    @Test
    public void getAllPokemons_returnsListOfPokemons() throws JsonProcessingException {
        when(pokemonService.getAllPokemons()).thenReturn(pokemons);
        ResponseEntity<List<Pokemon>> response = pokemonController.getAllPokemons();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(pokemons);
    }
}