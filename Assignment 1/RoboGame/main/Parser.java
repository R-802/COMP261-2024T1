package main;

import nodes.*;
import nodes.interfaces.ActionNode;
import nodes.interfaces.ProgramNode;
import nodes.interfaces.StatementNode;
import util.exepeptions.ParserFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * See assignment handout for the grammar.
 * You need to implement the parse(...) method and all the rest of the parser.
 * There are several methods provided for you:
 * - several utility methods to help with the parsing
 * See also the TestParser class for testing your code.
 */
public class Parser {

    // Useful Regex Patterns
    static final Pattern NUMPAT = Pattern.compile("-?[1-9][0-9]*|0");
    static final Pattern OPENPAREN = Pattern.compile("\\(");
    static final Pattern CLOSEPAREN = Pattern.compile("\\)");
    static final Pattern OPENBRACE = Pattern.compile("\\{");
    static final Pattern CLOSEBRACE = Pattern.compile("\\}");

    //----------------------------------------------------------------

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

    private ProgramNode parseProgram(Scanner s) {
        // Initialize an array of nodes
        List<StatementNode> nodes = new ArrayList<>();
        while (s.hasNext()) nodes.add(parseStatements(s));

        // Create a blocknode object to hold the whole program
        BlockNode blockNode = new BlockNode(nodes);
        return blockNode;
    }

    private StatementNode parseStatements(Scanner s) {
        if (!s.hasNext()) return null; // Handle end of input

        String token = s.next();
        switch (token) {
            case "move":
            case "turnL":
            case "turnR":
            case "takeFuel":
            case "wait":
                return parseAction(s, token);
            case "loop":
                return parseLoop(s);
            default:
                throw new ParserFailureException("Unexpected token (" + token + ")");
        }
    }

    private ActionNode parseAction(Scanner s, String action) {
        switch (action) {
            case "move":
                require(";", "missing semicolon", s);
                return new MoveNode();
            case "turnL":
                require(";", "missing semicolon", s);
                return new TurnLNode();
            case "turnR":
                require(";", "missing semicolon", s);
                return new TurnRNode();
            case "takeFuel":
                require(";", "missing semicolon", s);
                return new TakeFuelNode();
            case "wait":
                require(";", "missing semicolon", s);
                return new WaitNode();
            default:
                fail("Invalid action: ", s);
                return null;
        }
    }

    private StatementNode parseLoop(Scanner s) {
        require(OPENBRACE, "missing opening brace for loop", s);

        // Directly parse the statements inside the loop
        List<StatementNode> bodyStatements = new ArrayList<>();
        while (!s.hasNext(CLOSEBRACE)) {
            StatementNode statement = parseStatements(s);
            if (statement == null) {
                // Handle potential error in parseStatements
                throw new ParserFailureException("Unexpected null statement in loop body");
            }
            bodyStatements.add(statement);
        }

        // Ensure loop body isn't empty
        if (bodyStatements.isEmpty()) {
            throw new ParserFailureException("Loop body cannot be empty");
        }

        require(CLOSEBRACE, "missing closing brace for loop", s);
        return new LoopNode(new BlockNode(bodyStatements));
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
