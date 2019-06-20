package cn.seecoder;

public class Application extends AST {
    AST lhs;//左树
    AST rhs;//右树

    Application(AST l, AST s) {
        lhs = l;
        rhs = s;
    }


    public String toString() {
        return "(" + lhs.toString() + " " + rhs.toString() + ")";
    }
}
