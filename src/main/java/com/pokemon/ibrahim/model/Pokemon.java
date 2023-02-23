package com.pokemon.ibrahim.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pokemon {
    @JsonProperty("name")
    private String name;
    @JsonProperty("height")
    private int height;
    @JsonProperty("weight")
    private int weight;
    private int health;
    @JsonProperty("url")
    private String url;

    public void takeDamage(int damage) {
        this.health = Math.max(0, this.health - damage);
    }
}
