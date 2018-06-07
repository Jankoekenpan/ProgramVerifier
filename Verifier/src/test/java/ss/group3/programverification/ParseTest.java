package ss.group3.programverification;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;
import ss.group3.programverifier.LanguageLexer;
import ss.group3.programverifier.LanguageParser;

import java.io.IOException;

public class ParseTest {

    @Test
    public void testMax() throws IOException {
        CharStream input = new ANTLRFileStream("src/main/resources/max.hello");
        LanguageLexer lexer = new LanguageLexer(input);
        TokenStream stream = new CommonTokenStream(lexer);
        LanguageParser parser = new LanguageParser(stream);

        parser.program();
        //if execution succeeded without parse exceptions, then the test passes
    }

}
