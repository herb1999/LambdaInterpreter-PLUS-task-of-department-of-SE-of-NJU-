package cn.seecoder;

import java.util.ArrayList;

public class Parser {
    Lexer lexer;

    public Parser(Lexer l) {
        lexer = l;
    }

    public AST parse() {

        AST ast = term(new ArrayList<>());
//        System.out.println(lexer.match(TokenType.EOF));
        return ast;
    }

    private AST term(ArrayList<String> ctx) {
        if (lexer.next(TokenType.LAMBDA)) {
            lexer.nextToken();
            lexer.nextToken();
            Identifier param = new Identifier(lexer.getThisToken(), -1);
            ctx.add(0, lexer.getThisToken());
            lexer.nextToken();//to DOT

            return new Abstraction(param, term(ctx));

        } else {
            return application(ctx);
        }
    }

    //1.ctx切至右枝时要清空
    private AST application(ArrayList<String> ctx) {

        //改变application以适应左递归
        // application ::= atom application'
        // application' ::= atom application'
        //                | ε
        ArrayList<String> ctxSaved = new ArrayList<>(ctx);
        AST lhs = atom(ctx);
        ctx = new ArrayList<>(ctxSaved);
        AST rhs = atom(ctx);
        while (rhs != null) {
            lhs = new Application(lhs, rhs);
            ctx = new ArrayList<>(ctxSaved);
            rhs = atom(ctx);
        }
        return lhs;
    }

    private AST atom(ArrayList<String> ctx) {
        if (lexer.next(TokenType.LPAREN)) {
            lexer.nextToken();
            AST term = term(ctx);
            lexer.nextToken();

            return term;
        } else if (lexer.next(TokenType.LCID)) {

            String name = lexer.getThisToken();
            lexer.nextToken();
            int value = ctx.indexOf(name);

            return new Identifier(name, value);
        } else return null;

    }
}
