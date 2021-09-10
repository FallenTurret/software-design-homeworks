package ru.itmo.sd.visitors;

import ru.itmo.sd.tokens.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;

public class CalcVisitor implements TokenVisitor {
    private final Stack<Integer> values;
    private final Map<String, BiFunction<Integer, Integer, Integer>> operations;

    public CalcVisitor() {
        values = new Stack<>();
        operations = new HashMap<>();
        operations.put(new PlusToken().toString(), Integer::sum);
        operations.put(new MinusToken().toString(), (x, y) -> x - y);
        operations.put(new MultiplicationToken().toString(), (x, y) -> x * y);
        operations.put(new DivisionToken().toString(), (x, y) -> x / y);
    }

    @Override
    public void visit(NumberToken token) {
        values.push(token.getNumber());
    }

    @Override
    public void visit(BraceToken token) {
        throw new IllegalArgumentException("No braces should be here");
    }

    @Override
    public void visit(BinaryOperationToken token) {
        if (values.size() < 2) {
            throw new IllegalArgumentException("Too few arguments for binary operation");
        }
        var y = values.pop();
        var x = values.pop();
        values.push(operations.get(token.toString()).apply(x, y));
    }

    public int visitEnd() {
        if (values.size() != 1) {
            throw new IllegalArgumentException("Expression cannot be converted to a single number");
        }
        return values.pop();
    }
}
