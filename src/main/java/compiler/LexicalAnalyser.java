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
        List<String> tokens = new ArrayList<>();
        Pattern p = Pattern.compile("//.*|/[*]([\\d\\D]*)[*]/|([\"']).*?\\1|\\d+\\.\\d+|[.(){}\\[\\];></%!:^]|[+*/!-]=|([|&*=+-])\\1?|\\s+");
        Matcher m = p.matcher(file.replaceAll("", ""));
        int from = 0;
        while (m.find()) {
            if (from != m.start()) tokens.add(file.substring(from, m.start()));
            tokens.add(m.group());
            from = m.start() + m.group().length();
        }
        return parse(tokens);
    }

    private static List<Token> parse(List<String> strings) {
        return strings.stream().map(Token::new).filter(x -> {
            return !x.getType().equals(Token.Type.WhiteSpace) &&
                    !x.getType().equals(Token.Type.LineComment) &&
                    !x.getType().equals(Token.Type.MultiLineComment);
        }).collect(Collectors.toList());
    }
}
