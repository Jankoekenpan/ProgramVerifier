package ss.group3.programverification;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

import ss.group3.programverifier.LanguageLexer;
import ss.group3.programverifier.LanguageParser;
import ss.group3.programverifier.Z3Generator;

public class Z3GeneratorTest {
	private Z3Generator extracted() {
		CharStream input;
		try {
			input = CharStreams.fromFileName("src/main/resources/assign.hello");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		LanguageLexer lexer = new LanguageLexer(input);
		TokenStream stream = new CommonTokenStream(lexer);
		LanguageParser parser = new LanguageParser(stream);

		//parse tree
		LanguageParser.ProgramContext programTree = parser.program();
		
		Z3Generator generator = new Z3Generator();
		new ParseTreeWalker().walk(generator, programTree);
		return generator;
	}
	
	@Test
	public void test() throws IOException {
		Z3Generator generator = extracted();
		
		assertTrue(generator.isCorrect());
	}
}
