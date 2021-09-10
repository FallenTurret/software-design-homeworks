package ru.itmo.sd.tokens;

import ru.itmo.sd.visitors.TokenVisitor;

public interface Token {
    void accept(TokenVisitor visitor);
}
