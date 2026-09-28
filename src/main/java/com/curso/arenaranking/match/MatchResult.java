package com.curso.arenaranking.match;

public enum MatchResult {
    WIN,
    LOSS,
    DRAW;

    public static MatchResult calculate(int teamScore, int opponentScore) {
        if (teamScore > opponentScore) {
            return WIN;
        }
        if (teamScore < opponentScore) {
            return LOSS;
        }
        return DRAW;
    }
}
