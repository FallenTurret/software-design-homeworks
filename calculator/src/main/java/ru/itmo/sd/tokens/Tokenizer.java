package ru.itmo.sd.tokens;

import java.util.*;

public class Tokenizer {
    private final List<Token> tokens;
    private final StringBuilder curNumber;
    private final Scanner scanner;
    private final Map<Character, Token> symbolTokens;

    public Tokenizer() {
        tokens = new ArrayList<>();
        curNumber = new StringBuilder();
        scanner = new Scanner(System.in);
        symbolTokens = new HashMap<>();

        symbolTokens.put('(', new LeftBraceToken());
        symbolTokens.put(')', new RightBraceToken());
        symbolTokens.put('+', new PlusToken());
        symbolTokens.put('-', new MinusToken());
        symbolTokens.put('*', new MultiplicationToken());
        symbolTokens.put('/', new DivisionToken());
    }

    public void processSymbol(char c) {
        if (Character.isSpaceChar(c))
            return;
        if (Character.isDigit(c)) {
            curNumber.append(c);
            return;
        }
        if (curNumber.length() > 0) {
            var number = Integer.parseInt(curNumber.toString());
            curNumber.setLength(0);
            tokens.add(new NumberToken(number));
        }
        if (!symbolTokens.containsKey(c))
            throw new UnsupportedOperationException("Symbol " + c + "is not supported");
        tokens.add(symbolTokens.get(c));
    }

    public String readLineFromStdIn() {
        return scanner.nextLine();
    }

    public ListOfTokens getTokens() {
        if (curNumber.length() > 0) {
            var number = Integer.parseInt(curNumber.toString());
            curNumber.setLength(0);
            tokens.add(new NumberToken(number));
        }
        var res = new ListOfTokens(List.copyOf(tokens));
        tokens.clear();
        return res;
    }
}
