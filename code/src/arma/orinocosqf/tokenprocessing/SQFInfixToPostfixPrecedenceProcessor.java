package arma.orinocosqf.tokenprocessing;

import arma.orinocosqf.OrinocoToken;
import arma.orinocosqf.OrinocoTokenInstanceProcessor;
import arma.orinocosqf.lexer.OrinocoLexerContext;
import arma.orinocosqf.parsing.postfix.*;
import arma.orinocosqf.sqf.SQFCommand;
import arma.orinocosqf.sqf.SQFCommands;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static arma.orinocosqf.util.ASCIITextHelper.equalsIgnoreCase;

/**
 * @author K
 * @since 7/28/19
 */
public class SQFInfixToPostfixPrecedenceProcessor implements OrinocoTokenInstanceProcessor {
	private final List<OrinocoNode> postfixOutput = new ArrayList<>();
	private final Stack<OrinocoNode> operators = new Stack<>();
	private final Stack<DelayEvalOrinocoNode> delayEvalStack = new Stack<>();

	private int precedence(@NotNull SQFCommand cmd) {
		// ORDER OF PRECEDENCE: (Lower number is highest precedence)
		// https://community.bistudio.com/wiki/SQF_syntax
		// 1. Nular
		// 2. Unary commands (commandName OPERAND), unary '+' and '-' (+a, -a), '!' or 'not' ("!bool" or "not bool")
		// 3. # Select operator (array # index)
		// 4 ^ operator (a ^ b)
		// 5. '*', '/', '%', 'mod', config '/', 'atan2'
		// 6. '+','-','min','max'
		// 7. 'else' command
		// 8. binary commands and ':' operator
		// 9. '==','!=','>','<','>=','<=','>>'
		// 10. '&&' or 'and'
		// 11. '||' or 'or'
		if (cmd.isStrictlyNular()) {
			return 1;
		}
		if (cmd.isStrictlyUnary()) {
			return 2;
		}
		final String cn = cmd.getCommandName();
		if (postfixOutput.isEmpty() && cmd.canBeUnary() || (
				!postfixOutput.isEmpty() && (equalsIgnoreCase(cn, "+") || equalsIgnoreCase(cn, "-"))
		)
		) {
			return 2;
		}
		final SQFCommands.Operators ops = SQFCommands.ops();
		if (cmd == ops.HASH) {
			return 3;
		}
		if (cmd == ops.CARET) {
			return 4;
		}

		if (cmd == ops.ASTERISK ||
				cmd == ops.FSLASH ||
				cmd == ops.PERC ||
				equalsIgnoreCase(cn, "mod") ||
				equalsIgnoreCase(cn, "atan2")
		) {
			return 5;
		}
		if (cmd == ops.PLUS ||
				cmd == ops.MINUS ||
				equalsIgnoreCase(cn, "min") ||
				equalsIgnoreCase(cn, "max")
		) {
			return 6;
		}
		if (equalsIgnoreCase(cn, "else")) {
			return 7;
		}
		final boolean comp_or_conf_getter = cmd == ops.EQEQ
				|| cmd == ops.NE
				|| cmd == ops.GT
				|| cmd == ops.LT
				|| cmd == ops.LE
				|| cmd == ops.GE
				|| cmd == ops.GTGT;
		final boolean logical_and = cmd == ops.AMPAMP || equalsIgnoreCase(cn, "and");
		final boolean logical_or = cmd == ops.BARBAR || equalsIgnoreCase(cn, "or");
		final boolean special_binary = comp_or_conf_getter || logical_and || logical_or;
		if ((cmd.isStrictlyBinary() && !special_binary)
				|| cmd == ops.COLON) {
			return 8;
		}
		if (cmd.canBeBinary() && !special_binary && !postfixOutput.isEmpty()) {
			return 8;
		} else {
			//todo report error
		}
		if (comp_or_conf_getter) {
			return 9;
		}
		if (logical_and) {
			return 10;
		}
		if (logical_or) {
			return 11;
		}
		throw new IllegalStateException("Couldn't determine precedence for " + cn);
	}


	@Override
	public void begin(@NotNull OrinocoLexerContext ctx) {
		reset();
	}

	public void acceptNode(@NotNull OrinocoNode node) {
		// todo
	}

	@Override
	public void acceptCommand(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		SQFCommand command = SQFCommands.instance.getCommandInstanceById(token.getId());

		//check for parentheses
		final SQFCommands.Operators ops = SQFCommands.ops();

		if (!delayEvalStack.isEmpty()) {
			if (command == ops.L_SQ_BRACKET) {
				delayEvalStack.add(new DelayEvalOrinocoNode(new CommandOrinocoNode(command)));
				return;
			} else if (command == ops.R_SQ_BRACKET) {
				DelayEvalOrinocoNode delayPeek = delayEvalStack.peek();
				boolean invalidToken = false;
				if (delayPeek.getNode() instanceof CommandOrinocoNode) {
					CommandOrinocoNode cmdPeek = (CommandOrinocoNode) delayPeek.getNode();
					if (cmdPeek.getCommand() != ops.L_SQ_BRACKET) {
						invalidToken = true;
					}
				}
				if (invalidToken) {
					delayPeek = new InvalidTokenOrinocoNode(delayPeek);
				}
				delayEvalStack.pop();
				if (delayEvalStack.isEmpty()) {
					postfixOutput.add(delayPeek);
				} else {
					delayEvalStack.peek().getLastItemList().add(delayPeek);
				}
				return;
			} else if (command == ops.COMMA) {
				delayEvalStack.peek().getItems().add(new ArrayList<>());
				return;
			} else if (command == ops.L_CURLY_BRACE) {
				delayEvalStack.add(new DelayEvalOrinocoNode(new CommandOrinocoNode(command)));
				return;
			} else if (command == ops.R_CURLY_BRACE) {
				DelayEvalOrinocoNode delayPeek = delayEvalStack.peek();
				boolean invalidToken = false;
				if (delayPeek.getNode() instanceof CommandOrinocoNode) {
					CommandOrinocoNode cmdPeek = (CommandOrinocoNode) delayPeek.getNode();
					if (cmdPeek.getCommand() != ops.L_CURLY_BRACE) {
						invalidToken = true;
					}
				}
				if (invalidToken) {
					delayPeek = new InvalidTokenOrinocoNode(delayPeek);
				}
				delayEvalStack.pop();
				if (delayEvalStack.isEmpty()) {
					postfixOutput.add(delayPeek);
				} else {
					delayEvalStack.peek().getLastItemList().add(delayPeek);
				}
				return;
			} else if (command == ops.SEMICOLON) {
				delayEvalStack.peek().getItems().add(new ArrayList<>());
				return;
			} else {
				delayEvalStack.peek().getLastItemList().add(new CommandOrinocoNode(command));
			}

			return;
		}


		if (command == ops.L_SQ_BRACKET || command == ops.L_CURLY_BRACE) {
			delayEvalStack.add(new DelayEvalOrinocoNode(new CommandOrinocoNode(command)));
			return;
		}

		if (command == ops.R_SQ_BRACKET || command == ops.R_CURLY_BRACE) {
			postfixOutput.add(new InvalidTokenOrinocoNode(new CommandOrinocoNode(command)));
			return;
		}

		if (command == ops.QUEST) {
			// todo https://community.bistudio.com/wiki/a_:_b
		}

		if (command == ops.LPAREN) {
			operators.add(new CommandOrinocoNode(command));
		} else if (command == ops.RPAREN) {
			while (!operators.isEmpty()) {
				OrinocoNode peek = operators.peek();
				if (peek instanceof CommandOrinocoNode) {
					CommandOrinocoNode peekCmd = (CommandOrinocoNode) peek;
					if (peekCmd.getCommand() == ops.LPAREN) {
						break;
					}
				}
				postfixOutput.add(operators.pop());
			}
			if (!operators.isEmpty()) {
				OrinocoNode peek = operators.peek();
				if (peek instanceof CommandOrinocoNode) {
					CommandOrinocoNode peekCmd = (CommandOrinocoNode) peek;
					if (peekCmd.getCommand() != ops.LPAREN) {
						// todo report error
					} else {
						operators.pop();
					}
				} else {
					//todo report error because it's not (
				}
			}
		}

		int prec = precedence(command);
		// todo
	}

	private void addNode(@NotNull OrinocoNode node) {
		if (delayEvalStack.isEmpty()) {
			postfixOutput.add(node);
		} else {
			delayEvalStack.peek().getLastItemList().add(node);
		}
	}

	@Override
	public void acceptLocalVariable(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		addNode(new TokenOrinocoNode(OrinocoNode.Flag.Variable, token));
	}

	@Override
	public void acceptGlobalVariable(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		addNode(new TokenOrinocoNode(OrinocoNode.Flag.Variable, token));
	}

	@Override
	public void acceptLiteral(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		addNode(new TokenOrinocoNode(OrinocoNode.Flag.Literal, token));
	}

	@Override
	public void preProcessorTokenSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {
		throw new IllegalStateException("Skipping preprocessor token is not allowed for infix to postfix translation");
	}

	@Override
	public void preProcessorCommandSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {
		// Allow this because preprocessor commands don't affect SQF evaluation itself unless they create a
		// preprocessor token and said token is processed
	}

	@Override
	public void end(@NotNull OrinocoLexerContext ctx) {

	}

	@Override
	public void reset() {
		postfixOutput.clear();
	}

}
