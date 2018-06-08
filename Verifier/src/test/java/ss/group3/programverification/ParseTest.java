package ss.group3.programverification;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.junit.jupiter.api.Test;
import ss.group3.programverifier.LanguageLexer;
import ss.group3.programverifier.LanguageParser;

import java.io.IOException;
import java.util.BitSet;

public class ParseTest {

    //TODO fix grammar and/or input files so that these tests pass!
    private void parseFile(String filePath) throws IOException {
        CharStream input = new ANTLRFileStream(filePath);
        LanguageLexer lexer = new LanguageLexer(input);
        TokenStream stream = new CommonTokenStream(lexer);
        LanguageParser parser = new LanguageParser(stream);

        parser.program();
        //if execution succeeded without parse exceptions, then the test passes
    }

    @Test
    public void testMax() throws IOException {
        parseFile("src/main/resources/max.hello");
    }

    @Test
    public void testFactorialRecursive() throws IOException {
        parseFile("src/main/resources/factorialLoop.hello");
    }

    @Test
    public void testFactorialLoop() throws IOException {
        parseFile("src/main/resources/factorialRecursive.hello");
    }

}
