package ss.group3.programverification;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;
import ss.group3.programverifier.LanguageLexer;
import ss.group3.programverifier.LanguageParser;

import java.io.IOException;

public class ParseTest {

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
