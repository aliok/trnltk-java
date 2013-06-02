package org.trnltk.testutil;

public class TestEnvironment {

    private static final String HAS_BIG_PARSESETS = "hasBigParseSets";

    public static boolean hasBigParseSets(){
        return "true".equalsIgnoreCase(System.getProperty(HAS_BIG_PARSESETS));
    }

}
