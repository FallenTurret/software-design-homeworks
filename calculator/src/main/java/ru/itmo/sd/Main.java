package ru.itmo.sd;

import ru.itmo.sd.tokens.Tokenizer;
import ru.itmo.sd.visitors.CalcVisitor;
import ru.itmo.sd.visitors.ParserVisitor;
import ru.itmo.sd.visitors.PrintVisitor;

public class Main {
    public static void main(String[] args) {
        var tokenizer = new Tokenizer();
        var parser = new ParserVisitor();
        var printer = new PrintVisitor();
        var calculator = new CalcVisitor();
        for (char c: tokenizer.readLineFromStdIn().toCharArray()) {
            tokenizer.processSymbol(c);
        }
        var tokens = tokenizer.getTokens();
        tokens.accept(parser);
        tokens = parser.visitEnd();
        for (var t: tokens.getTokens()) {
            t.accept(printer);
            System.out.print(" ");
        }
        System.out.println();
        tokens.accept(calculator);
        System.out.println(calculator.visitEnd());
    }
}
