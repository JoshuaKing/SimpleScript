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
    public static List<Token> tokenize(String file) {
        List<Token> tokens = new ArrayList<>();
        Pattern p = Pattern.compile("//.*|([\"']).*?\\1|/[*][\\d\\D]*?[*]/|\\d+\\.\\d+|[.(){}\\[\\];></%!:^]|[+*/!-]=|([|&*=+-])\\1?|\\s+");

        String[] lines = file.split("(?=\\r?\\n)");
        for (int r = 0; r < lines.length; r++) {
            Matcher m = p.matcher(lines[r] + "\n");
            int from = 0;
            while (m.find()) {
                if (from != m.start()) tokens.add(new Token(lines[r].substring(from, m.start()), r, from - 1));
                tokens.add(new Token(m.group(), r, from));
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
