package main;

import nodes.BlockNode;
import nodes.LoopNode;
import nodes.NumberNode;
import nodes.WhileNode;
import nodes.conditionals.IfNode;
import nodes.conditionals.relops.EqualNode;
import nodes.conditionals.relops.GreaterThanNode;
import nodes.conditionals.relops.LessThanNode;
import nodes.interfaces.BooleanNode;
import nodes.interfaces.ProgramNode;
import nodes.movement.*;
import nodes.sensors.SensorNode;
import nodes.sensors.SensorType;
import util.exepeptions.ParserFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import static main.Parser.ParserUtil.require;

public class Parser {

    //----------------------------------------------------------------//
    //                            FIELDS                              //
    //----------------------------------------------------------------//

    // Patterns for the terminals in the grammar
    private static final Pattern NUMPAT = Pattern.compile("-?[1-9][0-9]*|0");
    private static final Pattern OPENPAREN = Pattern.compile("\\(");
    private static final Pattern CLOSEPAREN = Pattern.compile("\\)");
    private static final Pattern OPENBRACE = Pattern.compile("\\{");
    private static final Pattern CLOSEBRACE = Pattern.compile("}");
    private static final Pattern COMMA = Pattern.compile(",");
    private static final Pattern SEMICOLON = Pattern.compile(";");

    // Sets of valid actions and relational operators, used for parsing
    private static final Set<String> ACTIONS = Set.of("move", "turnL", "turnR", "takeFuel", "wait", "shieldOn", "shieldOff", "turnAround");
    private static final Set<String> RELOPS = Set.of("lt", "gt", "eq");

    // Error messages
    private static final String MISSING_SEMICOLON = "Missing semicolon";
    private static final String MISSING_OPEN_BRACE = "Missing opening brace for loop";
    private static final String MISSING_CLOSE_BRACE = "Missing closing brace for loop";
    private static final String MISSING_OPEN_PAREN = "Missing opening parenthesis";
    private static final String MISSING_CLOSE_PAREN = "Missing closing parenthesis";

    //----------------------------------------------------------------//
    //                            PARSER                              //
    //----------------------------------------------------------------//

    /**
     * The top of the parser, is handed a scanner containing
     * the text of the program to parse.
     * <p>
     * Returns the parse tree (ProgramNode) representing the program.
     */
    public ProgramNode parse(Scanner s) {
        s.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
        return parseProgram(s);
    }

    /**
     * Parses the program according to the grammar rule for PROG:
     * <p>
     * PROG ::= [STMT]*
     *
     * @param s Scanner positioned at the start of the program.
     * @return A ProgramNode representing the parsed program.
     */
    private ProgramNode parseProgram(Scanner s) {
        // Build a list to hold the parsed statement nodes from the scanner input
        List<ProgramNode> nodes = new ArrayList<>();
        while (s.hasNext()) { // Parse each statement in the program
            nodes.add(parseStatements(s));
        }

        // Return a new ProgramNode that contains each statement in the parsed program
        return robot -> { // Anonymous ProgramNode implementation
            for (ProgramNode statement : nodes) {
                statement.execute(robot);
            }
        };
    }

    /**
     * Parses statements according to the grammar rules for STMT:
     * <p>
     * STMT ::= ACT ";" | LOOP | IF | WHILE
     *
     * @param s Scanner positioned at the start of a statement.
     * @return A StatementNode representing the parsed statement.
     */
    private ProgramNode parseStatements(Scanner s) {
        String statementToken = s.next();

        // Check if the token is an action, loop, if, or while statement
        if (ACTIONS.contains(statementToken)) {
            return parseAction(s, statementToken); // ACT
        } else if ("loop".equals(statementToken)) {
            return parseLoop(s); // LOOP
        } else if ("if".equals(statementToken)) {
            return parseIf(s); // IF
        } else if ("while".equals(statementToken)) {
            return parseWhile(s); // WHILE
        } else {
            throw new ParserFailureException("Unexpected statement token: " + statementToken);
        }
    }

    /**
     * Parses an action from the given scanner input according to the specified grammar rule for ACT.
     * This method is called by {@link #parseStatements(Scanner)}.
     * <p>
     * Grammar rule:
     * ACT ::= "move" | "turnL" | "turnR" | "turnAround" | "shieldOn" | "shieldOff" | "takeFuel" | "wait"
     *
     * @param s The scanner containing the action to be parsed.
     * @return A {@code ProgramNode} representing the parsed action. Adjust the return type as necessary.
     */
    private ProgramNode parseAction(Scanner s, String action) {
        require(SEMICOLON, MISSING_SEMICOLON, s); // ";"

        // Check if the action token matches one of the valid action types and produce a new MovementNode
        return switch (action) {
            case "move" -> new MoveNode();
            case "turnL" -> new TurnLNode();
            case "turnR" -> new TurnRNode();
            case "turnAround" -> new TurnAroundNode();
            case "shieldOn" -> new ShieldOnNode();
            case "shieldOff" -> new ShieldOffNode();
            case "takeFuel" -> new TakeFuelNode();
            case "wait" -> new WaitNode();
            default -> throw new ParserFailureException("Unexpected action: " + action);
        };
    }

    /**
     * Parses a loop from the given scanner input according to the specified grammar rule for LOOP.
     * This method is called by {@link #parseStatements(Scanner)}.
     * <p>
     * Grammar rule:
     * LOOP ::= "loop" BLOCK
     *
     * @param s The scanner positioned at the "loop" keyword.
     * @return A {@code LoopNode} representing the parsed loop.
     */
    private LoopNode parseLoop(Scanner s) {
        // Curly brace check and block parse
        require(OPENBRACE, MISSING_OPEN_BRACE, s);
        BlockNode blockNode = parseBlock(s);
        require(CLOSEBRACE, MISSING_CLOSE_BRACE, s);

        return new LoopNode(blockNode);
    }

    /**
     * Parses an if statement from the given scanner input according to the specified grammar rule for IF.
     * This method is called by {@link #parseStatements(Scanner)}.
     * <p>
     * Grammar rule:
     * IF ::= "if" "(" COND ")" BLOCK
     *
     * @param s The scanner positioned at the "if" keyword.
     * @return An {@code IfNode} representing the parsed if statement.
     */
    private ProgramNode parseIf(Scanner s) {
        // Parentheses check and condition parse
        require(OPENPAREN, MISSING_OPEN_PAREN + " for IF", s);
        BooleanNode condition = parseCondition(s);
        require(CLOSEPAREN, MISSING_CLOSE_PAREN + " for IF", s);

        // Curly braces check and block parse
        require(OPENBRACE, MISSING_OPEN_BRACE + " for IF BLOCK", s);
        BlockNode ifBlock = parseBlock(s);
        require(CLOSEBRACE, MISSING_CLOSE_BRACE + " for IF BLOCK", s);

        return new IfNode(condition, ifBlock);
    }

    /**
     * Parses a while loop from the given scanner input according to the specified grammar rule for WHILE.
     * This method is called by {@link #parseStatements(Scanner)}.
     * <p>
     * Grammar rule:
     * WHILE ::= "while" "(" COND ")" BLOCK
     *
     * @param s The scanner positioned at the "while" keyword.
     * @return A {@code WhileNode} representing the parsed while loop.
     */
    private WhileNode parseWhile(Scanner s) {
        // Parentheses check and condition parse
        require(OPENPAREN, MISSING_OPEN_PAREN + " for WHILE", s);
        BooleanNode condition = parseCondition(s);
        require(CLOSEPAREN, MISSING_CLOSE_PAREN + " for WHILE", s);

        // Curly braces check and block parse
        require(OPENBRACE, MISSING_OPEN_BRACE + " for WHILE BLOCK", s);
        BlockNode ifBlock = parseBlock(s);
        require(CLOSEBRACE, MISSING_CLOSE_BRACE + " for WHILE BLOCK", s);

        return new WhileNode(condition, ifBlock);
    }

    /**
     * Parses a block from the given scanner input according to the specified grammar rule for BLOCK.
     * This method is called by {@link #parseLoop(Scanner)}, {@link #parseIf(Scanner)}, and {@link #parseWhile(Scanner)}.
     * <p>
     * Grammar rule:
     * BLOCK ::= "{" [STMT]* "}"
     *
     * @param s The scanner positioned at the start of a block.
     * @return A {@code BlockNode} representing the parsed block.
     */
    private BlockNode parseBlock(Scanner s) {
        List<ProgramNode> statements = new ArrayList<>();
        while (!s.hasNext(CLOSEBRACE)) { // Keep parsing statements until a close brace
            statements.add(parseStatements(s));
        }

        // Check if the block is empty and return a new BlockNode
        if (statements.isEmpty()) throw new ParserFailureException("Empty block");
        return new BlockNode(statements);
    }

    /**
     * Parse condition according to the grammar rule for COND:
     * <p>
     * COND ::= RELOP "(" SENS "," NUM ")"
     *
     * @param s Scanner positioned at the start of a condition.
     * @return A BooleanNode representing the parsed condition.
     */
    private BooleanNode parseCondition(Scanner s) {
        String relop = s.next();
        if (!RELOPS.contains(relop)) {
            throw new ParserFailureException("Expected relational operator but found: " + relop);
        }

        // "(" SENS "," NUM ")" parse
        require(OPENPAREN, MISSING_OPEN_PAREN + " for COND", s);
        SensorNode sensor = parseSensor(s);
        require(COMMA, "Expected comma", s);
        NumberNode number = parseNumber(s);
        require(CLOSEPAREN, MISSING_CLOSE_PAREN + " for COND", s);

        return parseRelop(relop, sensor, number);
    }

    /**
     * Parse a relational operator according to the grammar rule for RELOP:
     * <p>
     * RELOP ::= "lt" | "gt" | "eq"
     *
     * @param sensor The sensor node to compare.
     * @param number The number node to compare.
     * @return A BooleanNode representing the parsed relational operator.
     */
    private BooleanNode parseRelop(String relop, SensorNode sensor, NumberNode number) {
        return switch (relop) { // Check if the token matches one of the valid relational operators
            case "lt" -> new LessThanNode(sensor, number); // Return a new BooleanNode based on the relational operator
            case "gt" -> new GreaterThanNode(sensor, number);
            case "eq" -> new EqualNode(sensor, number);
            default -> throw new ParserFailureException("Unexpected relational operator: " + relop);
        };
    }

    /**
     * Parse a sensor according to the grammar rule for SENS:
     * <p>
     * SENS  ::= "fuelLeft" | "oppLR" | "oppFB" | "numBarrels" | "barrelLR" | "barrelFB" | "wallDist"
     *
     * @param s Scanner positioned at the start of a sensor.
     * @return A SensorNode representing the parsed sensor.
     */
    private SensorNode parseSensor(Scanner s) {
        if (!s.hasNext()) throw new ParserFailureException("Expected sensor but found end of input");
        String sensor = s.next(); // Get the sensor token

        // Check if the sensor token matches one of the sensor types
        return switch (sensor) {
            case "fuelLeft", "oppLR", "oppFB", "numBarrels", "barrelLR", "barrelFB", "wallDist" ->
                    new SensorNode(getSensorType(sensor));
            default -> throw new ParserFailureException("Expected sensor but found: " + sensor);
        };
    }

    /**
     * Helper method to convert a sensor string to a SensorType enum
     *
     * @param sensor The sensor string to convert
     * @return The corresponding SensorType enum
     */
    private static SensorType getSensorType(String sensor) {
        SensorType sensorType;
        switch (sensor) {
            case "fuelLeft" -> sensorType = SensorType.FUEL_LEFT;
            case "oppLR" -> sensorType = SensorType.OPP_LR;
            case "oppFB" -> sensorType = SensorType.OPP_FB;
            case "numBarrels" -> sensorType = SensorType.NUM_BARRELS;
            case "barrelLR" -> sensorType = SensorType.BARREL_LR;
            case "barrelFB" -> sensorType = SensorType.BARREL_FB;
            case "wallDist" -> sensorType = SensorType.WALL_DIST;
            default -> throw new IllegalStateException("Unexpected sensor type: " + sensor);
        }
        return sensorType;
    }

    /**
     * Parse a number according to the grammar rule for NUM:
     * NUM   ::= "-?[1-9][0-9]*|0"
     *
     * @param s Scanner positioned at the start of a number.
     * @return A NumberNode representing the parsed number.
     */
    private NumberNode parseNumber(Scanner s) {
        if (!s.hasNext(NUMPAT)) { // Check if the next token is a number
            throw new ParserFailureException("Expected number, found: " + s.next());
        }

        // Parse the number and return a new NumberNode
        int number = s.nextInt();
        return new NumberNode(number);
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
            StringBuilder msg = new StringBuilder(message + "\n   @ ...");
            for (int i = 0; i < 5 && s.hasNext(); i++) {
                msg.append(" ").append(s.next());
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
         * pattern, if so, consumes the token and return true. Otherwise, returns
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
