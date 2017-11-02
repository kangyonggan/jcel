package com.kangyonggan.jcel;

/**
 * @author kangyonggan
 * @since 11/2/17
 */
public class JCExpressionLiteral extends JCExpressionElement {

    private String constants;

    public String getConstants() {
        return constants;
    }

    public void setConstants(String constants) {
        this.constants = constants;
    }

    public JCExpressionLiteral() {
    }

    public JCExpressionLiteral(String constants) {
        this.constants = constants;
    }

    @Override
    public String toString() {
        return "JCExpressionLiteral{" +
                "constants='" + constants + '\'' +
                '}';
    }
}
