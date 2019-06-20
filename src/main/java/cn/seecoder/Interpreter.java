package cn.seecoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Interpreter {
    Parser parser;
    AST astAfterParser;
    static boolean isShowSteps;

    public Interpreter(Parser p) {
        parser = p;
        astAfterParser = p.parse();
        //System.out.println("After parser:"+astAfterParser.toString());
    }


    private boolean isAbstraction(AST ast) {
        return ast instanceof Abstraction;
    }

    private boolean isApplication(AST ast) {
        return ast instanceof Application;
    }

    private boolean isIdentifier(AST ast) {
        return ast instanceof Identifier;
    }


    public AST eval() {
        return evalAST(astAfterParser);
    }


    private AST evalAST(AST ast) {
        while (true) {
            if (isApplication(ast)) {
                Application app = (Application) ast;
                if (isIdentifier(app.lhs)) {
                    app.rhs = evalAST(app.rhs);
                    return ast;

                } else if (isAbstraction(app.lhs)) {
                    if (isApplication(app.rhs)) {
                        app.rhs = evalAST(app.rhs);
                    }
                    ast = substitute(((Abstraction) app.lhs).body, app.rhs);
                    if (isShowSteps){
                        System.out.println(astAfterParser.toString());
                    }
                } else {
                    app.lhs = evalAST(app.lhs);
                    app.rhs = evalAST(app.rhs);
                    if (isAbstraction(app.lhs)) {
                        ast = evalAST(app);
                    }
                    return ast;
                }
            } else if (isAbstraction(ast)) {
                Abstraction abs = (Abstraction) ast;
                abs.body = evalAST(abs.body);
//                if (isShowSteps) {
//                    System.out.println(astAfterParser.toString());
//                }
                return abs;
            } else {
                return ast;
            }
        }
    }

    private AST substitute(AST node, AST value) {

        return shift(-1, subst(node, shift(1, value, 0), 0), 0);


    }

    /**
     * value替换node节点中的变量：
     * 如果节点是Applation，分别对左右树替换；0
     * 如果node节点是abstraction，替入node.body时深度得+1；
     * 如果node是identifier，则替换De Bruijn index值等于depth的identifier（替换之后value的值加深depth）
     *
     * @param value 替换成为的value
     * @param node  被替换的整个节点
     * @param depth 外围的深度
     * @return AST
     * @throws  (方法有异常的话加)
     */

    private AST subst(AST node, AST value, int depth) {
        if (isApplication(node)) {
            Application app = (Application) node;
            return new Application(subst(app.lhs, value, depth), subst(app.rhs, value, depth));
        } else if (isAbstraction(node)) {

            Abstraction abs = (Abstraction) node;

            return new Abstraction(abs.param, subst(abs.body, value, depth + 1));
        } else {
            Identifier id = (Identifier) node;
            if (depth == id.value) {
                return shift(depth, value, 0);
            } else return node;
        }
    }

    /**
     * De Bruijn index值位移
     * 如果节点是Applation，分别对左右树位移；
     * 如果node节点是abstraction，新的body等于旧node.body位移by（from得+1）；
     * 如果node是identifier，则新的identifier的De Bruijn index值如果大于等于from则加by，否则加0（超出内层的范围的外层变量才要shift by位）.
     * <p>
     *    *@param by 位移的距离
     *
     * @param node 位移的节点
     * @param from 内层的深度
     *             <p>
     *                      
     * @return AST
     * @throws  (方法有异常的话加)
     */

    private AST shift(int by, AST node, int from) {
        if (isApplication(node)) {
            Application app = (Application) node;
            return new Application(shift(by, app.lhs, from), shift(by, app.rhs, from));

        } else if (isAbstraction(node)) {
            Abstraction abs = (Abstraction) node;
            return new Abstraction(abs.param, shift(by, abs.body, from + 1));

        } else {
            Identifier id = (Identifier) node;
            if (id.value >= from) {
                return new Identifier(id.name, id.value + by);
            } else return id;
        }
    }

    /**
     * @author 薛宗耀
     *
     */


    public static void main(String[] args) {
        // write your code here
        Preprocessor preprocessor = new Preprocessor();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String source;
        String sourcePreprocessed;

        while (true) {
            System.out.print("Lambda>");
            try {
                source = br.readLine();
                //退出
                if (source.contains("-q")) {
                    System.out.println("Exited.");
                    break;
                }
                //重置组合子
                else if (source.contains("-r")) {
                    preprocessor.reset();
                    System.out.println("Combinators reseted.");
                    continue;
                }
                //显示、不显示中间步骤，有点问题
                else if (source.contains("-s")) {
                    isShowSteps = !isShowSteps;
                    if (isShowSteps) {
                        System.out.println("Showing steps now.");
                    } else {
                        System.out.println("Stopping showing steps.");
                    }
                    continue;
                }
                //显示现有组合子
                else if (source.contains("-c")) {
                    preprocessor.showCombinators();
                    continue;
                }
                //增加或修改组合子
                else if (source.contains("=")) {
                    preprocessor.addCombinator(source);
                    System.out.println("Combinators changed.");
                    continue;
                }
                //删除组合子
                else if (source.contains("-d")) {
                    preprocessor.deleteCombinator(source);
                    System.out.println("Combinators deleted.");
                    continue;
                }
                //预处理（替换组合子）
                else {
                    sourcePreprocessed = preprocessor.preprocess(source);
                }

                Lexer lexer = new Lexer(sourcePreprocessed);

                Parser parser = new Parser(lexer);

                Interpreter interpreter = new Interpreter(parser);

                AST result = interpreter.eval();

                System.out.println(result.toString());

            } catch (IOException ex) {
                ex.printStackTrace();

            } catch (NullPointerException ex) {
                System.out.println("Invalid input...Nice try:)");
            }
        }
    }
}
/*

app 两支之间用括号隔开
-q 退出
-r 重置定义
-s 显示中间步骤 或 取消显示中间步骤
-c 显示现有组合子
-d 删除组合子(可以一次删除多个，用逗号隔开)
= 定义组合子

 */