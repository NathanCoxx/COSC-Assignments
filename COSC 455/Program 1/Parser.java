package COSC455.ParserExample_J11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* GRAMMAR FOR PROCESSING SIMPLE SENTENCES:

<SENTENCE>      ::= <NP> <VP> <NP> <PP> <SENTENCE_TAIL> 
<SENTENCE_TAIL> ::= <CONJ> <SENTENCE> <EOS> | <EOS>

<NP>       ::= <ART> <ADJ_LIST> <NOUN>
<ADJ_LIST> ::= <ADJ> <ADJ_TAIL> | <<EMPTY>>
<ADJ_TAIL> ::= <COMMA> <ADJ> <ADJ_TAIL> | <<EMPTY>>

<VP> ::= <ADV> <VERB> | <VERB>
<PP> ::= <PREP> <NP> | <<EMPTY>>

// *** Terminal Productions (Actual terminals omitted, but they are just the valid 
       words in the language). ***
<COMMA> ::= ','
<EOS>   ::= '.' | '!'
<ADJ>   ::= ...adjective list...
<ADV>   ::= ...adverb list...
<ART>   ::= ...article list...
<CONJ>  ::= ...conjunction list...
<NOUN>  ::= ...noun list...
<PREP>  ::= ...preposition list...
<VERB>  ::= ...verb list....
/*

/**
 * The Syntax Analyzer 
 */
class Parser {
    // The lexer which will provide the tokens

    private final LexicalAnalyzer lexer;
    private final CodeGenerator codeGenerator;

    /**
     * The constructor initializes the terminal literals in their vectors.
     *
     * @param lexer The Lexer Object
     */
    public Parser(LexicalAnalyzer lexer, CodeGenerator codeGenerator) {
        this.lexer = lexer;
        this.codeGenerator = codeGenerator;
    }

    /**
     * Begin analyzing...
     *
     * @throws MockCompiler.ParseException
     */
    public void analyze() {
        try {
            // Generate header for our output
            var startNode = codeGenerator.buildNode("PARSE TREE");
            codeGenerator.writeHeader(startNode);

            // Start the actual parsing.   
            Sentence(startNode);

            // generate footer for our output
            codeGenerator.writeFooter();

            // For graphically displaying the output.
            // CodeGenerator.openWebGraphViz();
        } catch (ParseException ex) {
            System.err.println("Syntax Error: " + ex);
        }
    }

    // <SENTENCE> ::= <NP> <VP> <NP> <PP> <SENTENCE_TAIL> 
    protected void Sentence(ParseNode fromNode) throws ParseException {
        final var nodeName = codeGenerator.buildNode("<SENTENCE>");
        codeGenerator.addNonTerminalToTree(fromNode, nodeName);

        NP(nodeName);
        VP(nodeName);
        NP(nodeName);
        PP(nodeName);
        Sentence_Tail(nodeName);
    }

    // <SENTENCE_TAIL> ::= <CONJ> <SENTENCE> | <EOS>
    void Sentence_Tail(ParseNode fromNode) throws ParseException {
        final var nodeName = codeGenerator.buildNode("<SENTENCE_TAIL>");
        codeGenerator.addNonTerminalToTree(fromNode, nodeName);

        if (lexer.isCurrentToken(TOKEN.CONJUNCTION)) {
            CONJ(nodeName);
            Sentence(nodeName);
        } else {
            EOS(fromNode);
        }
    }

    // <NP> ::= <ART> <ADJ_LIST> <NOUN>
    void NP(ParseNode fromNode) throws ParseException {
        final var nodeName = codeGenerator.buildNode("<NP>");
        codeGenerator.addNonTerminalToTree(fromNode, nodeName);

        ART(nodeName);
        ADJ_LIST(nodeName);
        NOUN(nodeName);
    }

    // <ADJ_LIST> ::= <ADJ> <ADJ_TAIL> | <<EMPTY>>
    void ADJ_LIST(ParseNode fromNode) throws ParseException {
        final var nodeName = codeGenerator.buildNode("<ADJ_LIST>");

        if (lexer.isCurrentToken(TOKEN.ADJECTIVE)) {
            codeGenerator.addNonTerminalToTree(fromNode, nodeName);
            ADJ(nodeName);
            ADJ_TAIL(nodeName);
        }
    }

    // <ADJ_TAIL> ::= <COMMA> <ADJ> <ADJ_TAIL> | <<EMPTY>>
    // NOTE: 
    void ADJ_TAIL(ParseNode fromNode) throws ParseException {
        final var nodeName = codeGenerator.buildNode("<ADJ_TAIL>");

        if (lexer.isCurrentToken(TOKEN.ADJ_SEP)) {
            codeGenerator.addNonTerminalToTree(fromNode, nodeName);
            ADJ_SEP(nodeName);

            if (lexer.isCurrentToken(TOKEN.ADJECTIVE)) {
                ADJ(nodeName);
                ADJ_TAIL(nodeName);
            }
        }
    }

    // <VP> ::= <ADV> <VERB> | <VERB>
    void VP(ParseNode fromNode) throws ParseException {
        final var nodeName = codeGenerator.buildNode("<VP>");
        codeGenerator.addNonTerminalToTree(fromNode, nodeName);

        if (lexer.isCurrentToken(TOKEN.ADVERB)) {
            ADV(nodeName);
        }

        VERB(nodeName);
    }

    // <PP> ::= <PREP> <NP> | <<EMPTY>>
    void PP(ParseNode fromNode) throws ParseException {
        final var nodeName = codeGenerator.buildNode("<PP>");

        if (lexer.isCurrentToken(TOKEN.PREPOSITION)) {
            codeGenerator.addNonTerminalToTree(fromNode, nodeName);
            PREP(nodeName);
            NP(nodeName);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // For the sake of completeness, each terminal-token has it's own method,
    // though they all do the same thing here.  In a "REAL" program, each terminal
    // would likely have unique code associated with it.
    /////////////////////////////////////////////////////////////////////////////////////
    // <EOS>
    void EOS(ParseNode fromNode) throws ParseException {
        ProcessTerminal(TOKEN.EOS, fromNode);
    }

    // <ADJ>
    void ADJ(ParseNode fromNode) throws ParseException {
        ProcessTerminal(TOKEN.ADJECTIVE, fromNode);
    }

    // <ADV> 
    void ADV(ParseNode fromNode) throws ParseException {
        ProcessTerminal(TOKEN.ADVERB, fromNode);
    }

    // <ART> 
    void ART(ParseNode fromNode) throws ParseException {
        ProcessTerminal(TOKEN.ARTICLE, fromNode);
    }

    // <CONJ> 
    void CONJ(ParseNode fromNode) throws ParseException {
        ProcessTerminal(TOKEN.CONJUNCTION, fromNode);
    }

    // <NOUN>
    void NOUN(ParseNode fromNode) throws ParseException {
        ProcessTerminal(TOKEN.NOUN, fromNode);
    }

    // <PREP>
    void PREP(ParseNode fromNode) throws ParseException {
        ProcessTerminal(TOKEN.PREPOSITION, fromNode);
    }

    // <VERB>
    void VERB(ParseNode fromNode) throws ParseException {
        ProcessTerminal(TOKEN.VERB, fromNode);
    }

    // <ADJ_SEP>
    void ADJ_SEP(ParseNode fromNode) throws ParseException {
        ProcessTerminal(TOKEN.ADJ_SEP, fromNode);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Terminal:
    // Test it's type and continute if we really have a terminal node, syntax error if fails.
    void ProcessTerminal(TOKEN terminal, ParseNode fromNode) throws ParseException {
        final var terminalID = codeGenerator.buildNode(String.format("<%s>", terminal));

        if (!lexer.isCurrentToken(terminal)) {
            raiseException(terminal, fromNode);
        } else {
            codeGenerator.addNonTerminalToTree(fromNode, terminalID);
            codeGenerator.addTerminalToTree(terminalID, lexer.getLexeme());
            lexer.advanceToken();
        }
    }

    // The code below this point is just a bunch of "helper functions" to keep the
    // parser code (above) a bit cleaner.
    // Handle all of the errors in one place for cleaner parser code.
    private void raiseException(TOKEN expected, ParseNode fromNode) throws ParseException {
        final var template = "SYNTAX ERROR: '%s' was expected but '%s' was found.";
        var err = String.format(template, expected.toString(), lexer.getLexeme());

        codeGenerator.syntaxError(err, fromNode);
    }

    static class ParseException extends Exception {

        public ParseException(String errMsg) {
            super(errMsg);
        }
    }
}

/**
 * All Of the Tokens/Terminals Used by the parser.
 * 
 * NOTE: IN MOST CASES, ***THERE WILL BE ONLY ONE LEXEME PER TOKEN*** The fact that several lexemes
 * exist per token in this example is because this is to parse simple English sentences where all
 * token types have many words that could fit. This is generally NOT the case in most programming
 * languages!!!
 */
enum TOKEN {
    ARTICLE("a", "the"),
    CONJUNCTION("and", "or"),
    NOUN("dog", "cat", "rat", "house", "tree"),
    VERB("loves", "hates", "eats", "chases", "stalks"),
    ADJECTIVE("fast", "slow", "furry", "sneaky", "lazy", "tall"),
    ADJ_SEP(","),
    ADVERB("quickly", "secretly", "silently"),
    PREPOSITION("of", "on", "around", "with", "up"),
    EOS(".", "!"),
    EOF, // End of file
    OTHER; // Something other than one of the above.

    private final List<String> lexemeList;

    private TOKEN(String... tokenStrings) {
        lexemeList = new ArrayList<>(tokenStrings.length);
        lexemeList.addAll(Arrays.asList(tokenStrings));
    }

    public static TOKEN fromLexeme(final String string) {
        // Just to be safe...
        var lexeme = string.trim();

        // An empty string should mean no more tokens to process.
        if (lexeme.isEmpty()) {
            return EOF;
        }

        // Search through ALL lexemes looking for a match with early bailout.
        for (var t : TOKEN.values()) {
            if (t.lexemeList.contains(lexeme)) {
                // early bailout.
                return t;
            }
        }

        // NOTE: Other could represent a number, for example.
        return OTHER;
    }
}
