package cn.seecoder;


public class Lexer {

    public String source;
    public int index;
    public TokenType token;
    public String tokenValue;

    public Lexer(String s) {
        index = 0;
        source = s;
        nextToken();
    }

    //get next token
    public TokenType nextToken() {
        char c;

        do {
            c = nextChar();
        } while ((c + "").matches("\\s+"));//除空格

        if ((c + "").matches("[a-z]")) {
            StringBuilder value = new StringBuilder();
            do {
                value.append(c);
                c = nextChar();
            } while ((c + "").matches("[a-zA-Z]"));
            if (c != '\0') {
                index--;
            }
            tokenValue = value.toString();
            this.token = TokenType.LCID;
//            System.out.println(this.token);
            return this.token;
        }
        switch (c) {
            case '(':
                this.token = TokenType.LPAREN;
                break;
            case ')':
                this.token = TokenType.RPAREN;
                break;
            case '.':
                this.token = TokenType.DOT;
                break;
            case '\\':
                this.token = TokenType.LAMBDA;
                break;
            case '\0':
                this.token = TokenType.EOF;
                break;
            default:
                return null;

        }
//        System.out.println(this.token);
        return this.token;
    }

    // get next char
    private char nextChar() {

        if (index < source.length()) {
            char result = source.charAt(index);
            index++;
            return result;
        } else {
            return '\0';
        }
    }


    //check token == t
    public boolean next(TokenType t) {
        return t == this.token;
    }

    public String getThisToken() {
        return this.tokenValue;
    }
}
