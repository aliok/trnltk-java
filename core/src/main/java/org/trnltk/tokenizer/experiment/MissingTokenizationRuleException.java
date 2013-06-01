package org.trnltk.tokenizer.experiment;

public class MissingTokenizationRuleException extends RuntimeException {
    private final TextBlockGroup leftTextBlockGroup;
    private final TextBlockGroup rightTextBlockGroup;
    private final TextBlockGroup contextBlockGroup;

    public MissingTokenizationRuleException(TextBlockGroup leftTextBlockGroup, TextBlockGroup rightTextBlockGroup, String msg, TextBlockGroup contextBlockGroup) {
        super(msg);
        this.leftTextBlockGroup = leftTextBlockGroup;
        this.rightTextBlockGroup = rightTextBlockGroup;
        this.contextBlockGroup = contextBlockGroup;
    }

    public TextBlockGroup getLeftTextBlockGroup() {
        return leftTextBlockGroup;
    }

    public TextBlockGroup getRightTextBlockGroup() {
        return rightTextBlockGroup;
    }

    public TextBlockGroup getContextBlockGroup() {
        return contextBlockGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MissingTokenizationRuleException that = (MissingTokenizationRuleException) o;

        if (!contextBlockGroup.equals(that.contextBlockGroup)) return false;
        if (!leftTextBlockGroup.equals(that.leftTextBlockGroup)) return false;
        if (!rightTextBlockGroup.equals(that.rightTextBlockGroup)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = leftTextBlockGroup.hashCode();
        result = 31 * result + rightTextBlockGroup.hashCode();
        result = 31 * result + contextBlockGroup.hashCode();
        return result;
    }
}
