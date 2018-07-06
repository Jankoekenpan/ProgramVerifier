# Program Verification

Jan Boerman (s1466518)
Michiel Bakker (s1492454)

# Installation

To build the program you need [JDK 8](http://openjdk.java.net/install/) or higher, [Maven](https://maven.apache.org/download.cgi) and [Z3 with Java bindings](https://github.com/Z3Prover/z3#java) should be installed. To build you need a working internet connection, then simply run `mvn install`. In order to check a language file, run `java -jar Verifier/target/Verifier-0.1.jar <path-to-file>`. Example programs can be found in `Verifier/src/main/resources`. The verifier has the following dependencies:

- Z3 Java API (included in the project). The Z3 API is used to generate the Z3 code. The verifier works by checking using the API, instead of generating a SMT file and using z3 after that. A limitation of this approach is that the assertions inside of a push/pop block cannot be accesed after a pop. This means that the full SMT code can't be printed after a run.
- ANTLR. This is a parser-generator used for the parsing of the language. It gets a grammar file as input, and generates Java code that parses the input files.
- JUnit. This is a unit testing framework that we've used to ensure that all verification features continued to work during development. All unit tests are run when the code is built. To run the test manually from your shell use `mvn test`. IDEs such as IntelliJ or Eclipse provide ways to run test cases individually.

# Language Specification

The program verifier we designed checks a simple programming language that is vaguely similar to C and the pseudocode used in the course manual. The language supports assignments, expressions, if-statements, while-loops, contracts and functions. An example program is given below.

```
int max(int x, int y)
# requires y > 0 #
# ensures (x >= y) => (\result == x) #
# ensures (y >= x) => (\result == y) #
{
	if (x > y) {
		return x;
	} else {
		return y;
	}
}

int r := max(1, 2);
# assert r == 2 #
```

The grammar of the language in EBNF form can be found in `Verifier/src/main/antlr4/ss/group3/programverifier/Language.g4`.

# Project structure

The following files contain the important classes of the verifier:

- `Verifier/src/main/java/ss/group3/programverifier/Z3Generator.java`. Traverses the AST and generates the Z3 symbols to verify the code.
- `Verifier/src/main/java/ss/group3/programverifier/Z3ExpressionParser.java`. Transforms expressions in the languge to Z3 code.
- `Verifier/src/test/java/ss/group3/programverification/Z3GeneratorTest.java`. The unit test of the verifier. Tests all features of the verifier, and checks if no wrong errors and all expected errors are found.

All classes are documented with comments to explain the details. Note that the verifier does not do any explicit type checking, i.e. it assumes that the checked program is type-safe (so no 1 + false).
All classes in the Z3-api module are taken from the Z3 project itself - which is released under the MIT licence. We did not make any modifications to those classes.
