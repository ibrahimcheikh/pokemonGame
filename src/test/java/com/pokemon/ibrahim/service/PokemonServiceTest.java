package com.pokemon.ibrahim.service;

import com.pokemon.ibrahim.mapper.PokemonMapper;
import com.pokemon.ibrahim.model.Pokemon;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PokemonServiceTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PokemonMapper pokemonMapper;

    @Autowired
    private PokemonService pokemonService;

    @Test
    public void testGetAllPokemons() {
        // Mock the response from the external API
        this.webTestClient = WebTestClient.bindToServer().baseUrl("https://pokeapi.co").build();
        this.webTestClient
                .get()
                .uri("/api/v2/pokemon?limit=50&offset=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    // Mock the response from the PokemonMapper
                    Pokemon expectedPokemon = new Pokemon("bulbasaur", 7, 69, 20, "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png");
                    try {
                        Mockito.when(pokemonMapper.mapJsonToPokemonEntity(Mockito.anyString())).thenReturn(expectedPokemon);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    // Call the method being tested
                    List<Pokemon> pokemons = null;
                    try {
                        pokemons = pokemonService.getAllPokemons();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    // Check that the correct Pokemon was returned
                    assertEquals(expectedPokemon, pokemons.get(0));
                });
    }
}