package cn.seecoder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Preprocessor {

    static String ZERO = "(\\f.\\x.x)";

    static String SUCC = "(\\n.\\f.\\x.f (n f x))";
    static String ONE = app(SUCC, ZERO);
    static String TWO = app(SUCC, ONE);
    static String THREE = app(SUCC, TWO);
    static String FOUR = app(SUCC, THREE);
    static String FIVE = app(SUCC, FOUR);
    static String PLUS = "(\\m.\\n.((m " + SUCC + ") n))";
    static String POW = "(\\b.\\e.e b)";       // POW not ready
    static String PRED = "(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))";
    static String SUB = "(\\m.\\n.n" + PRED + "m)";
    static String TRUE = "(\\x.\\y.x)";
    static String FALSE = "(\\x.\\y.y)";
    static String AND = "(\\p.\\q.p q p)";
    static String OR = "(\\p.\\q.p p q)";
    static String NOT = "(\\p.\\a.\\b.p b a)";
    static String IF = "(\\p.\\a.\\b.p a b)";
    static String ISZERO = "(\\n.n(\\x." + FALSE + ")" + TRUE + ")";
    static String LEQ = "(\\m.\\n." + ISZERO + "(" + SUB + "m n))";
    static String EQ = "(\\m.\\n." + AND + "(" + LEQ + "m n)(" + LEQ + "n m))";
    static String MAX = "(\\m.\\n." + IF + "(" + LEQ + " m n)n m)";
    static String MIN = "(\\m.\\n." + IF + "(" + LEQ + " m n)m n)";

    private static HashMap combinators = new HashMap();
    private static HashMap combinatorsSaved = new HashMap();

    static {

        combinators.put("ZERO", ZERO);
        combinators.put("ONE", ONE);
        combinators.put("TWO", TWO);
        combinators.put("THREE", THREE);
        combinators.put("FOUR", FOUR);
        combinators.put("FIVE", FIVE);
        combinators.put("SUCC", SUCC);
        combinators.put("PLUS", PLUS);
        combinators.put("POW", POW);
        combinators.put("PRED", PRED);
        combinators.put("SUB", SUB);
        combinators.put("TRUE", TRUE);
        combinators.put("FALSE", FALSE);
        combinators.put("AND", AND);
        combinators.put("OR", OR);
        combinators.put("NOT", NOT);
        combinators.put("IF", IF);
        combinators.put("ISZERO", ISZERO);
        combinators.put("LEQ", LEQ);
        combinators.put("EQ", EQ);
        combinators.put("MAX", MAX);
        combinators.put("MIN", MIN);
        combinatorsSaved.putAll(combinators);
    }

    private static String app(String func, String x) {
        return "(" + func + x + ")";
    }

//    private static String app(String func, String x, String y) {
//        return "(" + "(" + func + x + ")" + y + ")";
//    }
//
//    private static String app(String func, String cond, String x, String y) {
//        return "(" + func + cond + x + y + ")";
//    }

    public void addCombinator(String source) {
        String key = null;
        String value = null;
        if (source.matches(".*=.*")) {
            key = source.substring(0, source.indexOf('='));
            value = source.substring(source.indexOf('=') + 1);
        }
        combinators.put(key, value);
    }

    public void deleteCombinator(String source) {

        String[] deletedComs = source.split("\\s+|,");
        for (String com : deletedComs) {
            combinators.remove(com);
        }
    }

    public String preprocess(String source) {
        Iterator<String> it = combinators.keySet().iterator();

        while (it.hasNext()) {
            String key = it.next();
            String value = (String) combinators.get(key);
            value = value.replaceAll("\\\\", "\\\\\\\\");//防止接下来替换时\消失
//            System.out.println(value);
            source = source.replaceAll("\\b" + key + "\\b", value);
        }
        return source;
    }

    public void showCombinators() {
        Iterator<String> it = combinators.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            System.out.println(key + " : " + combinators.get(key));
        }
    }

    public void reset() {
        combinators.clear();
        combinators.putAll(combinatorsSaved);
    }
}
