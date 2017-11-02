package com.kangyonggan.jcel;

/**
 * @author kangyonggan
 * @since 11/2/17
 */
public class JCExpressionIdent extends JCExpressionElement {

    private String variableName;

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public JCExpressionIdent() {
    }

    public JCExpressionIdent(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String toString() {
        return "JCExpressionIdent{" +
                "variableName='" + variableName + '\'' +
                '}';
    }
}
