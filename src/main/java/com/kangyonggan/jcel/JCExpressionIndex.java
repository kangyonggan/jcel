package com.kangyonggan.jcel;

/**
 * @author kangyonggan
 * @since 11/2/17
 */
public class JCExpressionIndex extends JCExpressionElement {

    private String index;

    private String type;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JCExpressionIndex() {
    }

    public JCExpressionIndex(String index, String type) {
        this.index = index;
        this.type = type;
    }

    @Override
    public String toString() {
        return "JCExpressionIndex{" +
                "index='" + index + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
