package compiler;

import classes.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by josking on 3/2/16.
 */
public class LexicalAnalyser {
    private enum RegexExp {
        LineComment("//.*"),
        String("([\"']).*?\\1"),
        MultiLineComment("/[*][\\d\\D]*?[*]/"),
        Float("\\d+\\.\\d+"),
        SpecialAssign("[+*/!-]="),
        SingleAndDoubleCharacters("([|&*=+-])\\2?"),
        KeyCharacters("[\\(\\)\\{\\}\\[\\];></%!:,\\^]"),
        Whitespace("\\s+");

        String pattern;

        RegexExp(String pattern) {
            this.pattern = pattern;
        }
    }

    public static List<Token> tokenize(String file) {
        List<Token> tokens = new ArrayList<>();
        String pattern = "";

        for (RegexExp p : RegexExp.values()) {
            pattern += p.pattern + "|";
        }
        pattern = pattern.substring(0, pattern.length() - 1);
        System.out.println("REGEX: " + pattern);
        Pattern p = Pattern.compile(pattern);

        String[] lines = file.split("(?=\\r?\\n)");
        for (int r = 0; r < lines.length; r++) {
            Matcher m = p.matcher(lines[r] + "\n");
            int from = 0;
            while (m.find()) {
                if (from != m.start()) tokens.add(new Token(lines[r].substring(from, m.start()), r, from - 1));
                tokens.add(new Token(m.group(), r, from - 1));
                from = m.start() + m.group().length();
            }
        }
        return tokens.stream().filter(x -> {
            return !x.getType().equals(Token.Type.WhiteSpace) &&
                    !x.getType().equals(Token.Type.LineComment) &&
                    !x.getType().equals(Token.Type.MultiLineComment);
        }).collect(Collectors.toList());
    }
}
