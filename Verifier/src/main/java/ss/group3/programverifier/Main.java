package ss.group3.programverifier;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

public class Main {

    public static void main(String[] args) throws IOException {
    	if (args.length != 1) {
    		System.err.println("Usage: java -jar Verifier.jar <path-to-file>");
    		
    		return;
    	}
    	
    	CharStream input;
		try {
			input = CharStreams.fromFileName(args[0]);
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
		
		for (ProgramError error : generator.getErrors()) {
			System.out.println(error);
		}
    }
}
