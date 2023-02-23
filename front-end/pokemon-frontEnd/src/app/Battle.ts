import { Player } from "./Player";

export interface Battle {
    players:Player[];
    currentRound:number;
    winnter:Player;
}