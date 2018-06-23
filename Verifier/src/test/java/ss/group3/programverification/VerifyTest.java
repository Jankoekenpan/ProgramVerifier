package ss.group3.programverification;

import java.io.IOException;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Test;

import ss.group3.programverifier.LanguageLexer;
import ss.group3.programverifier.LanguageParser;
import ss.group3.programverifier.LanguageProver;
import ss.group3.programverifier.ToAstCompiler;
import ss.group3.programverifier.ast.Program;

public class VerifyTest {

    @Test
    public void verifyMax() throws IOException {
        CharStream input = new ANTLRFileStream("src/main/resources/max.hello");
        LanguageLexer lexer = new LanguageLexer(input);
        TokenStream stream = new CommonTokenStream(lexer);
        LanguageParser parser = new LanguageParser(stream);

        LanguageParser.ProgramContext programContext = parser.program();
        assert programContext != null;

        ToAstCompiler toAstCompiler = new ToAstCompiler();
        Program program = (Program) toAstCompiler.visit(programContext);
        assert program != null;

        LanguageProver languageProver = new LanguageProver();
        assert languageProver.proveProgram(program);
    }

}
