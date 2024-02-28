package main;

import nodes.*;
import nodes.interfaces.ActionNode;
import nodes.interfaces.ProgramNode;
import nodes.interfaces.StatementNode;
import util.exepeptions.ParserFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * See assignment handout for the grammar.
 * You need to implement the parse(...) method and all the rest of the parser.
 * There are several methods provided for you:
 * - several utility methods to help with the parsing
 * See also the TestParser class for testing your code.
 */
public class Parser {

    // Patterns for the terminals in the grammar
    private static final Pattern NUMPAT = Pattern.compile("-?[1-9][0-9]*|0");
    private static final Pattern OPENPAREN = Pattern.compile("\\(");
    private static final Pattern CLOSEPAREN = Pattern.compile("\\)");
    private static final Pattern OPENBRACE = Pattern.compile("\\{");
    private static final Pattern CLOSEBRACE = Pattern.compile("\\}");

    // Non-terminal symbols
    private static final Set<String> ACTIONS = Set.of("move", "turnL", "turnR", "takeFuel", "wait");

    // Error messages
    private static final String MISSING_SEMICOLON = "Missing semicolon";
    private static final String MISSING_OPEN_BRACE = "Missing opening brace for loop";
    private static final String MISSING_CLOSE_BRACE = "Missing closing brace for loop";

    /**
     * The top of the parser, which is handed a scanner containing
     * the text of the program to parse.
     * Returns the parse tree (ProgramNode) representing the program.
     */
    public ProgramNode parse(Scanner s) {
        // Set the delimiter for the scanner to recognize tokens separated by whitespace, parentheses, curly braces, commas, and semicolons.
        s.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");

        // Call the parseProg method for the first grammar rule (PROG) and return the parsed program node
        return parseProgram(s);
    }

    /**
     * Parses the program according to the grammar rule for PROG ::= [STMT]*
     *
     * @param s Scanner positioned at the start of the program.
     * @return A ProgramNode representing the parsed program.
     */
    private ProgramNode parseProgram(Scanner s) {
        // Build a list to hold the parsed statement nodes from the scanner input
        List<StatementNode> nodes = new ArrayList<>();
        while (s.hasNext()) nodes.add(parseStatements(s));

        // Return a new ProgramNode that contains each statement in the parsed program
        return new ProgramNode() {
            @Override
            public void execute(Robot robot) { // Execute each statement in the program
                for (StatementNode statement : nodes) {
                    statement.execute(robot);
                }
            }
        };
    }

    /**
     * Parses statements according to the grammar rules for STMT:
     * STMT ::= ACT ";" | LOOP
     *
     * @param s Scanner positioned at the start of a statement.
     * @return A StatementNode representing the parsed statement.
     */
    private StatementNode parseStatements(Scanner s) {
        // Check for end of input
        if (!s.hasNext()) throw new ParserFailureException("End of input reached unexpectedly");
        String token = s.next(); // Get the next token from the scanner

        // Check if the token is an action or a loop
        if (ACTIONS.contains(token)) {
            return parseAction(s, token);
        } else if ("loop".equals(token)) {
            return parseLoop(s);
        } else {
            throw new ParserFailureException("Unexpected token (" + token + ")");
        }
    }

    /**
     * Parses an action according to the grammar rule for ACT:
     * ACT ::= "move" | "turnL" | "turnR" | "takeFuel" | "wait"
     *
     * @param s      Scanner positioned after the action keyword.
     * @param action The action keyword.
     * @return An ActionNode representing the parsed action.
     */
    private ActionNode parseAction(Scanner s, String action) {
        require(";", MISSING_SEMICOLON, s);

        // Return a new ActionNode based on the action keyword
        switch (action) {
            case "move":
                return new MoveNode();
            case "turnL":
                return new TurnLNode();
            case "turnR":
                return new TurnRNode();
            case "takeFuel":
                return new TakeFuelNode();
            case "wait":
                return new WaitNode();
            default:
                throw new ParserFailureException("Invalid action: " + action);
        }
    }

    /**
     * Parses a loop according to the grammar rule for LOOP:
     * LOOP ::= "loop" BLOCK
     *
     * @param s Scanner positioned at the "loop" keyword.
     * @return A LoopNode representing the parsed loop.
     */
    private StatementNode parseLoop(Scanner s) {
        require(OPENBRACE, MISSING_OPEN_BRACE, s);
        BlockNode blockNode = parseBlock(s);
        require(CLOSEBRACE, MISSING_CLOSE_BRACE, s);
        return new LoopNode(blockNode);
    }

    /**
     * Parses a block according to the grammar rule for BLOCK:
     * BLOCK ::= "{" STMT+ "}"
     *
     * @param s Scanner positioned at the start of a block.
     * @return A BlockNode representing the parsed block.
     */
    private BlockNode parseBlock(Scanner s) {
        // Build a list to hold the parsed statement nodes from the scanner input
        List<StatementNode> statements = new ArrayList<>();
        while (!s.hasNext(CLOSEBRACE)) statements.add(parseStatements(s));
        if (statements.isEmpty()) throw new ParserFailureException("Block cannot be empty");

        // Return a new BlockNode that contains each statement in the parsed block
        return new BlockNode(statements);
    }

    //----------------------------------------------------------------
    // utility methods for the parser
    // - fail(..) reports a failure and throws exception
    // - require(..) consumes and returns the next token as long as it matches the pattern
    // - requireInt(..) consumes and returns the next token as an int as long as it matches the pattern
    // - checkFor(..) peeks at the next token and only consumes it if it matches the pattern

    /**
     * Report a failure in the parser.
     */
    static void fail(String message, Scanner s) {
        String msg = message + "\n   @ ...";
        for (int i = 0; i < 5 && s.hasNext(); i++) {
            msg += " " + s.next();
        }
        throw new ParserFailureException(msg + "...");
    }

    /**
     * Requires that the next token matches a pattern if it matches, it consumes
     * and returns the token, if not, it throws an exception with an error
     * message
     */
    static String require(String p, String message, Scanner s) {
        if (s.hasNext(p)) {
            return s.next();
        }
        fail(message, s);
        return null;
    }

    static String require(Pattern p, String message, Scanner s) {
        if (s.hasNext(p)) {
            return s.next();
        }
        fail(message, s);
        return null;
    }


    /**
     * Requires that the next token matches a pattern (which should only match a
     * number) if it matches, it consumes and returns the token as an integer
     * if not, it throws an exception with an error message
     */
    static int requireInt(String p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {
            return s.nextInt();
        }
        fail(message, s);
        return -1;
    }

    static int requireInt(Pattern p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {
            return s.nextInt();
        }
        fail(message, s);
        return -1;
    }

    /**
     * Checks whether the next token in the scanner matches the specified
     * pattern, if so, consumes the token and return true. Otherwise returns
     * false without consuming anything.
     */
    static boolean checkFor(String p, Scanner s) {
        if (s.hasNext(p)) {
            s.next();
            return true;
        }
        return false;
    }

    static boolean checkFor(Pattern p, Scanner s) {
        if (s.hasNext(p)) {
            s.next();
            return true;
        }
        return false;
    }
}
