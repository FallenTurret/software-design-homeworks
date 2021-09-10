package ru.itmo.sd.visitors;

import ru.itmo.sd.tokens.*;

import java.util.*;

public class ParserVisitor implements TokenVisitor {
    private final Stack<Token> operations;
    private final List<Token> tokens;
    private final Map<String, Integer> priority;

    public ParserVisitor() {
        operations = new Stack<>();
        tokens = new ArrayList<>();
        priority = new HashMap<>();

        priority.put(new PlusToken().toString(), 2);
        priority.put(new MinusToken().toString(), 2);
        priority.put(new MultiplicationToken().toString(), 1);
        priority.put(new DivisionToken().toString(), 1);
    }

    @Override
    public void visit(NumberToken token) {
        tokens.add(token);
    }

    @Override
    public void visit(BraceToken token) {
        if (token instanceof LeftBraceToken) {
            operations.push(token);
        } else {
            if (operations.empty()) {
                throw new IllegalArgumentException("Negative bracket balance on prefix of the expression");
            }
            while (true) {
                var op = operations.pop();
                if (op instanceof LeftBraceToken)
                    break;
                tokens.add(op);
                if (operations.empty()) {
                    throw new IllegalArgumentException("Negative bracket balance on prefix of the expression");
                }
            }
        }
    }

    @Override
    public void visit(BinaryOperationToken token) {
        while (!operations.empty()) {
            var last = operations.peek();
            if (last instanceof BinaryOperationToken &&
                priority.get(last.toString()) <= priority.get(token.toString())) {
                tokens.add(operations.pop());
            } else {
                break;
            }
        }
        operations.push(token);
    }

    public ListOfTokens visitEnd() {
        while (!operations.empty()) {
            var op = operations.pop();
            if (op instanceof LeftBraceToken) {
                throw new IllegalArgumentException("Positive bracket balance");
            }
            tokens.add(op);
        }
        var result = new ListOfTokens(List.copyOf(tokens));
        tokens.clear();
        return result;
    }
}
