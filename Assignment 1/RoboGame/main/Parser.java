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

import static main.Parser.ParserUtil.*;

public class Parser {

    //----------------------------------------------------------------//
    //                            FIELDS                              //
    //----------------------------------------------------------------//

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

    //----------------------------------------------------------------//
    //                            PARSER                              //
    //----------------------------------------------------------------//

    /**
     *  TODO: Add a grammar for the language (STAGE 1)
     *  * <pre>
     *  * PROG  ::= [ STMT ]*
     *  * STMT  ::= ACT ";" | LOOP | IF | WHILE
     *  * ACT   ::= "move" | "turnL" | "turnR" | "turnAround" | "shieldOn" |
     *  *           "shieldOff" | "takeFuel" | "wait"
     *  * LOOP  ::= "loop" BLOCK
     *  * IF    ::= "if" "(" COND ")" BLOCK
     *  * WHILE ::= "while" "(" COND ")" BLOCK
     *  * BLOCK ::= "{" STMT+ "}"
     *  * COND  ::= RELOP "(" SENS "," NUM ")
     *  * RELOP ::= "lt" | "gt" | "eq"
     *  * SENS  ::= "fuelLeft" | "oppLR" | "oppFB" | "numBarrels" |
     *  *           "barrelLR" | "barrelFB" | "wallDist"
     *  * NUM   ::= "-?[1-9][0-9]*|0"
     *  * <pre>
     */

    /**
     * The top of the parser, which is handed a scanner containing
     * the text of the program to parse.
     * Returns the parse tree (ProgramNode) representing the program.
     */
    public ProgramNode parse(Scanner s) {
        s.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
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

    //----------------------------------------------------------------//
    //                           UTILITY                              //
    //----------------------------------------------------------------//

    /**
     * Utility methods for the parser
     *
     * <ul>
     *   <li>{@link #fail(String, Scanner)} - Throws an exception for parsing failures.</li>
     *   <li>{@link #require(String, String, Scanner)}, {@link #require(Pattern, String, Scanner)} - Checks and consumes a token if it matches a specified pattern, otherwise throws an error.</li>
     *   <li>{@link #requireInt(String, String, Scanner)}, {@link #requireInt(Pattern, String, Scanner)} - Validates and converts a token to an integer if it matches a numerical pattern, else throws an error.</li>
     *   <li>{@link #checkFor(String, Scanner)}, {@link #checkFor(Pattern, Scanner)} - Peeks and optionally consumes a token if it fits a given pattern, returning a boolean.</li>
     * </ul>
     */
    public static class ParserUtil {

        /**
         * Report a failure in the parser.
         */
        public static void fail(String message, Scanner s) {
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
        public static String require(String p, String message, Scanner s) {
            if (s.hasNext(p)) {
                return s.next();
            }
            fail(message, s);
            return null;
        }

        public static String require(Pattern p, String message, Scanner s) {
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
        public static int requireInt(String p, String message, Scanner s) {
            if (s.hasNext(p) && s.hasNextInt()) {
                return s.nextInt();
            }
            fail(message, s);
            return -1;
        }

        public static int requireInt(Pattern p, String message, Scanner s) {
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
        public static boolean checkFor(String p, Scanner s) {
            if (s.hasNext(p)) {
                s.next();
                return true;
            }
            return false;
        }

        public static boolean checkFor(Pattern p, Scanner s) {
            if (s.hasNext(p)) {
                s.next();
                return true;
            }
            return false;
        }
    }
}
