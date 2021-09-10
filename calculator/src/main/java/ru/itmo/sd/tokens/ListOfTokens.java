package ru.itmo.sd.tokens;

import ru.itmo.sd.visitors.TokenVisitor;

import java.util.List;

public class ListOfTokens {
    private final List<Token> tokens;

    public ListOfTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void accept(TokenVisitor visitor) {
        for (var token: tokens) {
            token.accept(visitor);
        }
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
