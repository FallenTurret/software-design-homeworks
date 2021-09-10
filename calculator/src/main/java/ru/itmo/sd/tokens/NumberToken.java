package ru.itmo.sd.tokens;

import ru.itmo.sd.visitors.TokenVisitor;

public class NumberToken implements Token {
    private final int number;

    public NumberToken(int number) {
        this.number = number;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "NUMBER(" + number + ")";
    }

    public int getNumber() {
        return number;
    }
}
