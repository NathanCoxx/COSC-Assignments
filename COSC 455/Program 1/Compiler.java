package COSC455.ParserExample_J11;

import static java.lang.System.err;
import static java.lang.System.out;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

/**
 * COSC 455 Programming Languages: Implementation and Design.
 * <p>
 * A Simple Lexical Analyzer Adapted from Sebesta (2010) by Josh Dehlinger (2010-2012) further
 * modified by Adam J. Conover (2012-2020)
 * <p>
 * This syntax analyzer implements a top-down, left-to-right, recursive-descent parser based on the
 * production rules for the simple English language provided by Weber in "Modern Programming
 * Languages".
 */
public class Compiler {

    // NOTE: THESE ARE ONLY FOR TESTING!!! 
    private static final String DEFAULT_TEST_SENTENCE = "the dog chases the slow , lazy cat up a tall tree and the rat loves the dog .";
    //private static final String DEFAULT_TEST_SENTENCE = "the lazy dog quickly chases the fast , sneaky , furry cat up a tall tree and the rat loves the dog .";
    //private static final String DEFAULT_TEST_SENTENCE = "the dog chases the cat .";

    public static void main(String[] args) throws Parser.ParseException {
        LexicalAnalyzer lexicalAnalyzer;
        CodeGenerator codeGenerator = new CodeGenerator();

        // If passed a file, process the file, else use the default TESTING string.
        if (args.length == 1) {
            final File file = new File(args[0]);
            if (file.exists()) {
                lexicalAnalyzer = new LexicalAnalyzer(file);
            } else {
                err.printf("Input file not found: %s%n", file.toPath());
                return;
            }
        } else {
            err.printf("No Filename Provided!  Using HARD-CODED test string instead:%n\t\"%s\"%n%n", DEFAULT_TEST_SENTENCE);
            lexicalAnalyzer = new LexicalAnalyzer(DEFAULT_TEST_SENTENCE);
        }

        // Compile the program from the input supplied by the lexical analyzer.
        var parser = new Parser(lexicalAnalyzer, codeGenerator);
        parser.analyze();

     
        // Warn if the output was not the result of parsing a file.
        if (args.length == 0) {
            out.println("\nWARNING: No INPUT FILE was actually parsed!");
        }
        
        // UNCOMMENT THE CodeGenerator LINE BELOW TO AUTOMATIACLLY OPEN THE WebGraphviz SITE.
        // (You may want to consider just installing the "desktop" version.)
        // CodeGenerator.openWebGraphViz(true);
    }
}

/**
 * Fake Lexical Analyzer... NOTE: This DOES NOT "lex" the input in the traditional manner! Instead
 * of using "state transitions", it is merely a quick hack to create a something that BEHAVES like a
 * traditional lexer in it's USAGE, but it only knows how to separate (tokenize) words delimited by
 * spaces. A Real Lexer would tokenize based upon far more sophisticated lexical rules.
 * <p>
 * AGAIN: ALL TOKENS MUST BE WHITESPACE DELIMITED.
 */
class LexicalAnalyzer {

    // TOKENIZED input.
    Queue<TokenString> tokenList;

    // Just a "Pair Tuple/Struct" for the token type and original string.
    class TokenString {

        private final String lexeme;
        private final TOKEN token;

        public TokenString(String lexeme) {
            this.lexeme = lexeme;
            this.token = TOKEN.fromLexeme(lexeme);
        }

        @Override
        public String toString() {
            var msg = String.format("{lexeme=%s, token=%s}", lexeme, token);
            return msg;
        }
    }

    // Simple Wrapper around current token.
    boolean isCurrentToken(TOKEN token) {
        return token == getToken();
    }

    /**
     * Construct a lexer over an input string.
     *
     * @param inputString
     */
    public LexicalAnalyzer(String inputString) {
        tokenize(inputString);
    }

    /**
     * Construct a Lexer over the contents of a file. Filters out lines starting with a '#' Symbol.
     * Removes EOL markers since. (Otherwise, our grammar would have to deal with them).
     *
     * @param inputFile
     */
    public LexicalAnalyzer(File inputFile) {
        try {
            tokenize(Files.lines(inputFile.toPath())
                    .filter(x -> !x.startsWith("#"))
                    .collect(Collectors.joining(" ")));
        } catch (IOException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error Reading File: {0}", ex);
        }
    }

    /* Convert the line to a series of tokens. */
    private void tokenize(final String line) {
        // Using Java 8's "Function Streams"
        this.tokenList = Arrays
                .stream(line.trim().split("\\s+"))
                .map(TokenString::new)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /* Get just the lexeme */
    public String getLexeme() {
        return this.tokenList.isEmpty() ? "" : this.tokenList.peek().lexeme;
    }

    /* get just the token */
    public TOKEN getToken() {
        return this.tokenList.isEmpty() ? TOKEN.EOF : this.tokenList.peek().token;
    }

    /* Advance to next token, making it current. */
    public void advanceToken() {
        if (!this.tokenList.isEmpty()) {
            this.tokenList.remove();
        }
    }

    @Override
    public String toString() {
        return this.tokenList.toString();
    }
}

/**
 * A "Tuple" for the node name and id number.
 * <p>
 */
class ParseNode {

    String nodeName;
    Integer nodeId;
    static Integer currentNodeID = 0;

    public ParseNode(String nodeName) {
        this.nodeName = nodeName;
        this.nodeId = currentNodeID++;
    }

    public String getNodeName() {
        return nodeName;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    @Override
    public String toString() {
        return String.format("%s-%s", this.nodeName, this.nodeId);
    }
}

/**
 * This is a SIMULATION of a "code generator" that simply generates GraphViz output. Technically,
 * this would represent be the "Intermediate Code Generation" step.
 *
 * @author aconover
 */
class CodeGenerator {

    // Show our progress as we go...
    public void addNonTerminalToTree(ParseNode fromNode, ParseNode toNode) {
        out.printf("\t\"%s\" -> {\"%s\" [label=\"%s\", shape=rect]};%n", fromNode, toNode, toNode.nodeName);
    }

    // Show the terminals as ovals...
    public void addTerminalToTree(ParseNode fromNode, String lexeme) {
        var node = new ParseNode(lexeme);
        out.printf("\t\"%s\" -> {\"%s\" [label=\"%s\", shape=oval]};%n", fromNode, node, lexeme);
    }

    // Call this if a syntax error occurs...
    public void syntaxError(String err, ParseNode fromNode) throws Parser.ParseException {
        out.printf("\t\"%s\" -> {\"%s\"};%n}%n", fromNode, err);
        throw new Parser.ParseException(err);
    }

    // Build a node name so it can be later "deconstructed" for the output.
    public ParseNode buildNode(String name) {
        return new ParseNode(name);
    }

    public void writeHeader(ParseNode node) {
        // The header for the "compiled" output
        out.println("digraph ParseTree {");
        out.printf("\t{\"%s\" [label=\"%s\", shape=diamond]};\n", node, node.nodeName);
    }

    public void writeFooter() {
        out.println("}");
    }

    /**
     * To open a browser window...
     * <p>
     * FEEL FREE TO IGNORE THIS!!! It's just for opening the default browser, if desired.
     */
    static void openWebGraphViz(boolean promptToOpen) {
        final var WEBGRAPHVIZ_HOME = "http://www.webgraphviz.com/";

        final var MSG
                = "To visualize the output, Copy/Paste the \n"
                + "parser output into: http://www.webgraphviz.com\n";

        // Open the default browser with the url:
        try {
            final URI webGraphvizURI = new URI(WEBGRAPHVIZ_HOME);
            final Desktop desktop = Desktop.getDesktop();

            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
                if (promptToOpen) {
                    out.println(MSG);
                    var response = JOptionPane.showConfirmDialog(null, MSG + "\nOpen Web Graphviz Page?", "Open Web Graphviz Page", JOptionPane.YES_NO_OPTION);

                    if (response == JOptionPane.YES_OPTION) {
                        desktop.browse(webGraphvizURI);
                    }
                } else {
                    desktop.browse(webGraphvizURI);
                }
            }
        } catch (IOException | URISyntaxException ex) {
            java.util.logging.Logger.getAnonymousLogger().log(java.util.logging.Level.WARNING, "Could not open browser", ex);
        }
    }
}
