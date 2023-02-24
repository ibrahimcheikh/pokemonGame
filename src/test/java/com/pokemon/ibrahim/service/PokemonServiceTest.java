package com.pokemon.ibrahim.service;

import com.pokemon.ibrahim.mapper.PokemonMapper;
import com.pokemon.ibrahim.model.Battle;
import com.pokemon.ibrahim.model.Player;
import com.pokemon.ibrahim.model.Pokemon;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
                    Pokemon expectedPokemon = new Pokemon("bulbasaur", 7, 69, 20, "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png","https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png\"");
                    try {
                        when(pokemonMapper.mapJsonToPokemonEntity(Mockito.anyString())).thenReturn(expectedPokemon);
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

    @Test
    void testNormalAttack() {
        int damage = pokemonService.normalAttack();
        assertTrue(damage >= 1 && damage <= 10);
    }
    @Test
    void testSpecialAttack() {
        int damage = pokemonService.specialAttack();
        assertTrue(damage >= 5 && damage <= 15);
    }

    @Test
    void testResetHealth() {

        Pokemon pokemon = new Pokemon("Bulbasaur", 50,100,20,"pokebowl.com","https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png\"");
        Player player = new Player("Misty", pokemon,0);
        pokemonService.resetHealth(player);
        assertEquals(pokemon.getHealth(), 20);
    }

    @Test
    void testChooseFirstPlayer() {
        int result = pokemonService.chooseFirstPlayer();
        assertTrue(result == 0 || result == 1);
    }

    @Test
    public void testHandleRounds() {
        Player player1 = new Player("Ash", new Pokemon("Pikachu", 100,80,20,"batata.com","https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png\""),0);
        Player player2 = new Player("Ibrahim", new Pokemon("Dinozor", 69,50,20,"goo.com","https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png\""),0);
        List<Player>players = new ArrayList<>(2);
        players.add(player1);
        players.add(player2);
        StringBuilder sb = new StringBuilder();
        Battle battle = new Battle(players,null,sb);
        int turnsCount = 0;
        pokemonService.handleRounds(player1, player2, sb, turnsCount,battle);
        assertTrue(player1.getWonRounds() == 1 || player2.getWonRounds() == 1);
        assertTrue(player1.getPokemon().getHealth() > 0 || player2.getPokemon().getHealth() > 0);

}
    @Test
    void start_Game_Returns_Battle() {
        PokemonService pokemonService = Mockito.mock(PokemonService.class);
        //Preparing the test prerequisites
        Player player1 = new Player("Ash", new Pokemon("Pikachu", 100,80,20,"batata.com","https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png\""),0);
        Player player2 = new Player("Ibrahim", new Pokemon("Dinozor", 69,50,20,"goo.com","https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png\""),0);
        List<Player>players = new ArrayList<>(2);
        players.add(player1);
        players.add(player2);
        StringBuilder sb = new StringBuilder();
        Battle expectedBattle = new Battle(players, null,sb);
        //Expected a Battle Object to be returned
        when(pokemonService.start(players)).thenReturn(expectedBattle);
        Battle actualBattle = pokemonService.start(players);
        //It should be same assertion
        assertEquals(expectedBattle, actualBattle);
        //Only the method is called once
        verify(pokemonService, times(1)).start(players);
    }

    @Test
    public void testGetAllPlayers() {
        Player player1 = new Player("Ash", new Pokemon("Pikachu", 100, 80, 20, "batata.com", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"), 0);
        Player player2 = new Player("Ibrahim", new Pokemon("Dinozor", 69, 50, 20, "goo.com", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"), 0);
        List<Player> players = Arrays.asList(player1, player2);
        pokemonService.setPlayers(players);
        List<Player> result = pokemonService.getAllPlayers();
        assertEquals(players, result);
    }

    @Test
    public void testFindPlayerByName() {
        List<Player>  players = Arrays.asList(
                new Player("Ash", new Pokemon("Pikachu", 100, 80, 20, "batata.com", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"), 0),
                new Player("Ibrahim", new Pokemon("Dinozor", 69, 50, 20, "goo.com", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"), 0)
        );
        pokemonService.setPlayers(players);
        Player result = pokemonService.findPlayerByName("Ibrahim");
        assertEquals(players.get(1), result);
        result = pokemonService.findPlayerByName("Brock");
        assertNull(result);
    }

}