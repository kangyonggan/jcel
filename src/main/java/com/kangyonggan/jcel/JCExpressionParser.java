package com.kangyonggan.jcel;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Name;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kangyonggan
 * @since 11/2/17
 */
public class JCExpressionParser {

    /**
     * empty String
     */
    private static final String EMPTY = "";

    /**
     * expression's prefix
     */
    private static final String EXPR_PREFIX = "$";

    /**
     * expression's begin
     */
    private static final String EXPR_BEGIN = "{";

    /**
     * expression's end
     */
    private static final String EXPR_END = "}";

    /**
     * expression's get
     */
    private static final String EXPR_GET = ".";

    /**
     * expression's array begin
     */
    private static final String EXPR_ARR_BEGIN = "[";

    /**
     * expression's array end
     */
    private static final String EXPR_ARR_END = "]";

    /**
     * expression's list begin
     */
    private static final String EXPR_LIST_BEGIN = "(";

    /**
     * expression's list end
     */
    private static final String EXPR_LIST_END = ")";

    private TreeMaker treeMaker;
    private Name.Table names;

    public JCExpressionParser(TreeMaker treeMaker, Name.Table names) {
        this.treeMaker = treeMaker;
        this.names = names;
    }

    /**
     * parse expression
     *
     * @param express
     */
    public JCTree.JCExpression parse(String express) {
        List<JCExpressionElement> exprElements = new ArrayList();

        StringBuilder word = new StringBuilder();
        String exprSign = EMPTY;

        for (int i = 0; i < express.length(); i++) {
            String str = express.substring(i, i + 1);
            switch (str) {
                case EXPR_PREFIX: {// $
                    if (isNotEmpty(word.toString())) {
                        exprElements.add(new JCExpressionLiteral(word.toString()));
                        word = new StringBuilder();
                    }
                    exprSign = str;
                    break;
                }
                case EXPR_BEGIN: {// {
                    exprSign = str;
                    break;
                }
                case EXPR_END: {// }
                    if (in(exprSign, EXPR_BEGIN, EXPR_GET, EMPTY)) {
                        exprElements.add(new JCExpressionIdent(word.toString()));
                        word = new StringBuilder();
                    }

                    exprSign = str;
                    break;
                }
                case EXPR_GET: {// .
                    if (!in(exprSign, EXPR_ARR_END, EXPR_LIST_END)) {
                        exprElements.add(new JCExpressionIdent(word.toString()));
                        word = new StringBuilder();
                    }

                    exprSign = str;
                    break;
                }
                case EXPR_ARR_BEGIN: {// [
                    if (!in(exprSign, EXPR_ARR_END, EXPR_LIST_END)) {
                        exprElements.add(new JCExpressionIdent(word.toString()));
                        word = new StringBuilder();
                    }

                    exprSign = str;
                    break;
                }
                case EXPR_ARR_END: {// ]
                    exprElements.add(new JCExpressionIndex(word.toString(), str));
                    word = new StringBuilder();

                    exprSign = str;
                    break;
                }
                case EXPR_LIST_BEGIN: {// (
                    if (!in(exprSign, EXPR_ARR_END, EXPR_LIST_END)) {
                        exprElements.add(new JCExpressionIdent(word.toString()));
                        word = new StringBuilder();
                    }

                    exprSign = str;
                    break;
                }
                case EXPR_LIST_END: {// )
                    exprElements.add(new JCExpressionIndex(word.toString(), str));
                    word = new StringBuilder();

                    exprSign = str;
                    break;
                }
                default: {// other
                    word.append(str);
                }
            }
        }

        if (isNotEmpty(word.toString())) {
            exprElements.add(new JCExpressionLiteral(word.toString()));
        }

        return convert(exprElements);
    }

    private JCTree.JCExpression convert(List<JCExpressionElement> exprElements) {
        JCTree.JCExpression expression = null;

        for (int i = 0; i < exprElements.size(); i++) {
            JCExpressionElement element = exprElements.get(i);

            if (element instanceof JCExpressionLiteral) {
                JCExpressionLiteral literal = (JCExpressionLiteral) element;
                if (expression == null) {
                    expression = treeMaker.Literal(literal.getConstants());
                } else {
                    expression = treeMaker.Binary(JCTree.Tag.PLUS, expression, treeMaker.Literal(literal.getConstants()));
                }
            } else if (element instanceof JCExpressionIdent) {
                JCExpressionIdent ident = (JCExpressionIdent) element;

                JCTree.JCExpression expr = treeMaker.Ident(names.fromString(ident.getVariableName()));
                for (i = i + 1; i < exprElements.size(); i++) {
                    JCExpressionElement nextElement = exprElements.get(i);
                    if (nextElement instanceof JCExpressionIdent) {
                        String methodName = "get" + toClassName(((JCExpressionIdent) nextElement).getVariableName());
                        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(expr, names.fromString(methodName));
                        expr = treeMaker.Apply(com.sun.tools.javac.util.List.nil(), fieldAccess, com.sun.tools.javac.util.List.nil());
                    } else if (nextElement instanceof JCExpressionIndex) {
                        JCExpressionIndex index = (JCExpressionIndex) nextElement;
                        if (index.getType().equals(EXPR_ARR_END)) {
                            expr = treeMaker.Indexed(expr, treeMaker.Ident(names.fromString(index.getIndex())));
                        } else if (index.getType().equals(EXPR_LIST_END)) {
                            JCTree.JCFieldAccess fieldAccess = treeMaker.Select(expr, names.fromString("get"));
                            expr = treeMaker.Apply(com.sun.tools.javac.util.List.nil(), fieldAccess, com.sun.tools.javac.util.List.of(treeMaker.Ident(names.fromString(index.getIndex()))));
                        }
                    } else {
                        break;
                    }
                }
                i--;

                if (expression == null) {
                    expression = expr;
                } else {
                    expression = treeMaker.Binary(JCTree.Tag.PLUS, expression, expr);
                }
            }
        }

        return expression == null ? treeMaker.Literal(EMPTY) : expression;
    }

    private boolean in(String data, String... arr) {
        for (String str : arr) {
            if (str.equals(data)) {
                return true;
            }
        }

        return false;
    }

    private String toClassName(String varName) {
        return varName.substring(0, 1).toUpperCase() + varName.substring(1);
    }

    private boolean isNotEmpty(String data) {
        return data != null && data.trim().length() > 0;
    }

}
