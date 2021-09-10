package ru.itmo.sd.visitors;

import ru.itmo.sd.tokens.BinaryOperationToken;
import ru.itmo.sd.tokens.BraceToken;
import ru.itmo.sd.tokens.NumberToken;

public class PrintVisitor implements TokenVisitor {
    @Override
    public void visit(NumberToken token) {
        System.out.print(token);
    }

    @Override
    public void visit(BraceToken token) {
        System.out.print(token);
    }

    @Override
    public void visit(BinaryOperationToken token) {
        System.out.print(token);
    }
}
