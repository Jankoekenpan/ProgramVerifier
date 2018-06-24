package ss.group3.programverification;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Test;

import ss.group3.programverifier.LanguageLexer;
import ss.group3.programverifier.LanguageParser;
import ss.group3.programverifier.ProgramError;
import ss.group3.programverifier.Z3Generator;

public class Z3GeneratorTest {
	private void checkFile(String fileName, Integer... expectedLines) {
		CharStream input;
		try {
			input = CharStreams.fromFileName(fileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		LanguageLexer lexer = new LanguageLexer(input);
		TokenStream stream = new CommonTokenStream(lexer);
		LanguageParser parser = new LanguageParser(stream);

		//parse tree
		LanguageParser.ProgramContext programTree = parser.program();
		
		Z3Generator generator = new Z3Generator();
		generator.visit(programTree);
		
		ArrayList<Integer> expected = new ArrayList<>();
		expected.addAll(Arrays.asList(expectedLines));
		
		ArrayList<ProgramError> unexpected = new ArrayList<>();
		unexpected.addAll(generator.getErrors());
		
		for (ProgramError error : generator.getErrors()) {
			Integer line = stream.get(error.getSource().getSourceInterval().a).getLine();
			if(expected.contains(line)) {
				expected.remove(line);
				unexpected.remove(error);
			}
		}
		
		for (ProgramError error : unexpected) {
			System.out.println("Error with contract " + error.getSource().getText());
			System.out.println(error.getSmt());
			System.out.println(error.getModel());
		}
		
		for (Integer line : expected) {
			System.out.printf("Expected error on line %s, got none\n", line);
		}

		assertTrue(expected.isEmpty());
		assertTrue(unexpected.isEmpty());
	}
	
	@Test
	public void testAssign() {
		checkFile("src/main/resources/assign.hello", 4);
	}
	
	@Test
	public void testConditional() {
		checkFile("src/main/resources/conditional.hello");
	}
}
