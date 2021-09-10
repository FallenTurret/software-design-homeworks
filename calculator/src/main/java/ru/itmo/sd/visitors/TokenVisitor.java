package ru.itmo.sd.visitors;

import ru.itmo.sd.tokens.BinaryOperationToken;
import ru.itmo.sd.tokens.BraceToken;
import ru.itmo.sd.tokens.NumberToken;

public interface TokenVisitor {
    void visit(NumberToken token);
    void visit(BraceToken token);
    void visit (BinaryOperationToken token);
}
