package ss.group3.programverification;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;
import ss.group3.programverifier.LanguageLexer;
import ss.group3.programverifier.LanguageParser;
import ss.group3.programverifier.ToAstCompiler;
import ss.group3.programverifier.ast.Program;

import java.io.IOException;

public class ParseTest {

    @Test
    public void testMax() throws IOException {
        CharStream input = new ANTLRFileStream("src/main/resources/max.hello");
        LanguageLexer lexer = new LanguageLexer(input);
        TokenStream stream = new CommonTokenStream(lexer);
        LanguageParser parser = new LanguageParser(stream);

        LanguageParser.ProgramContext programContext = parser.program();
        assert programContext != null;

        ToAstCompiler toAstCompiler = new ToAstCompiler();
        Program program = (Program) toAstCompiler.visit(programContext);
        assert program != null;
    }

    @Test
    public void testFactorialRecursive() throws IOException {
        CharStream input = new ANTLRFileStream("src/main/resources/factorialLoop.hello");
        LanguageLexer lexer = new LanguageLexer(input);
        TokenStream stream = new CommonTokenStream(lexer);
        LanguageParser parser = new LanguageParser(stream);

        LanguageParser.ProgramContext programContext = parser.program();
        assert programContext != null;

        ToAstCompiler toAstCompiler = new ToAstCompiler();
        Program program = (Program) toAstCompiler.visit(programContext);
        assert program != null;
    }

    @Test
    public void testFactorialLoop() throws IOException {
        CharStream input = new ANTLRFileStream("src/main/resources/factorialRecursive.hello");
        LanguageLexer lexer = new LanguageLexer(input);
        TokenStream stream = new CommonTokenStream(lexer);
        LanguageParser parser = new LanguageParser(stream);

        LanguageParser.ProgramContext programContext = parser.program();
        assert programContext != null;

        ToAstCompiler toAstCompiler = new ToAstCompiler();
        Program program = (Program) toAstCompiler.visit(programContext);
        assert program != null;
    }

    @Test
    public void testCount() throws IOException {
        CharStream input = new ANTLRFileStream("src/main/resources/count.hello");
        LanguageLexer lexer = new LanguageLexer(input);
        TokenStream stream = new CommonTokenStream(lexer);
        LanguageParser parser = new LanguageParser(stream);

        LanguageParser.ProgramContext programContext = parser.program();
        assert programContext != null;

        ToAstCompiler toAstCompiler = new ToAstCompiler();
        Program program = (Program) toAstCompiler.visit(programContext);
        assert program != null;
    }
}
