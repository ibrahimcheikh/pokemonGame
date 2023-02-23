package com.pokemon.ibrahim.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemon.ibrahim.model.Pokemon;
import org.springframework.stereotype.Component;

@Component
public class PokemonMapper {

    public Pokemon mapJsonToPokemonEntity(String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        String name = jsonNode.get("name").asText();
        int height = jsonNode.get("height").asInt();
        int weight = jsonNode.get("weight").asInt();
        String urlFront = jsonNode.get("sprites").get("front_default").asText();
        String urlBack = jsonNode.get("sprites").get("back_default").asText();
        //By default all pokemons have a start of 20 health
        return new Pokemon(name, height, weight,20,urlFront,urlBack);
    }
}
