package Grammar;

import classes.Expression;
import classes.Variable;

import java.util.List;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarMethodParameters extends GrammarRule<Boolean> {
    private List<Variable> parameters;

    public GrammarMethodParameters(List<Variable> parameters) throws GrammarException {
        this.parameters = parameters;
        ensure(notNull(parameters));
    }

    @Override
    public Boolean parseGrammar() throws GrammarException {

        for (int i = 0; i < parameters.size(); i++) {
            Variable parameter = parameters.get(i);
            Expression expression = required(new GrammarExpression(parameter.getType()));
            System.out.println("Parameter #" + i);

            if (notNull(expression) && expression.getVariableType().equals(parameter.getType())) {
                except("Parameter #" + i + " with of type '" + expression.getVariableType().name() + "' does not match Argument '" + parameter.getName() + "' of type '" + parameter.getType().name() + "'");
            } else if (expression == null) {
                except("Not enough parameters for method: expected " + parameters.size() + " but only have " + i);
            }
        }
        return true;
    }
}
