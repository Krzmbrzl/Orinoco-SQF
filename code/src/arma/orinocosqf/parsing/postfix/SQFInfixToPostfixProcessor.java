package arma.orinocosqf.parsing.postfix;

import arma.orinocosqf.Command;
import arma.orinocosqf.OrinocoSQFTokenType;
import arma.orinocosqf.OrinocoToken;
import arma.orinocosqf.OrinocoTokenInstanceProcessor;
import arma.orinocosqf.lexer.OrinocoLexerContext;
import arma.orinocosqf.sqf.SQFCommand;
import arma.orinocosqf.sqf.SQFCommands;
import arma.orinocosqf.syntax.CommandSyntax;
import arma.orinocosqf.type.ValueType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static arma.orinocosqf.util.ASCIITextHelper.equalsIgnoreCase;

/**
 * @author K
 * @since 7/28/19
 */
public class SQFInfixToPostfixProcessor implements OrinocoTokenInstanceProcessor {
	private boolean canBeUnary = false;
	private final Stack<Command> operators = new Stack<>();
	private final Stack<DelayEvalOrinocoNode> delayEvalStack = new Stack<>();
	private final List<InfixPatternMatcher> matchers = new ArrayList<>();
	private OrinocoNode leftNode, rightNode;
	private ValueType leftType, rightType;
	private CommandOrinocoNode op;

	public SQFInfixToPostfixProcessor() {
	}

	private int precedence(@NotNull Command cmd) {
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
		final String cn = cmd.getName();
		if (canBeUnary && cmd.canBeUnary()) {
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
		if (cmd.canBeBinary() && !special_binary) {
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

	@Override
	public void acceptCommand(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		processOrinocoToken(token, ctx);

		SQFCommand command = SQFCommands.instance.getCommandInstanceById(token.getId());
		if (command == null) {
			throw new IllegalStateException();
		}

		final SQFCommands.Operators ops = SQFCommands.ops();

		if (!delayEvalStack.isEmpty()) {
			if (command == ops.L_SQ_BRACKET) {
				delayEvalStack.add(new DelayEvalOrinocoNode(OrinocoNode.Flag.Array, new CommandOrinocoNode(command)));
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
				if (!invalidToken) {
					delayEvalStack.pop();
				}
				if (delayEvalStack.isEmpty()) {
					processNode(delayPeek);
				} else {
					delayEvalStack.peek().getLastItemList().add(delayPeek);
				}
				return;
			} else if (command == ops.COMMA) {
				DelayEvalOrinocoNode peek = delayEvalStack.peek();
				boolean error = false;
				if (peek.getNode() instanceof CommandOrinocoNode) {
					CommandOrinocoNode cmdPeek = (CommandOrinocoNode) peek.getNode();
					if (cmdPeek.getCommand() != ops.L_SQ_BRACKET) {
						error = true;
					}
				}
				if (error) {
					delayEvalStack.peek().getLastItemList().add(new InvalidTokenOrinocoNode(new CommandOrinocoNode(command)));
				} else {
					delayEvalStack.peek().getLastItemList().add(new CommandOrinocoNode(command));
					delayEvalStack.peek().getItems().add(new ArrayList<>());
				}
				return;
			} else if (command == ops.L_CURLY_BRACE) {
				delayEvalStack.add(new DelayEvalOrinocoNode(OrinocoNode.Flag.CodeBlock, new CommandOrinocoNode(command)));
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
				if (!invalidToken) {
					delayEvalStack.pop();
				}
				if (delayEvalStack.isEmpty()) {
					processNode(delayPeek);
				} else {
					delayEvalStack.peek().getLastItemList().add(delayPeek);
				}
				return;
			} else if (command == ops.SEMICOLON) {
				DelayEvalOrinocoNode peek = delayEvalStack.peek();
				boolean error = false;
				if (peek.getNode() instanceof CommandOrinocoNode) {
					CommandOrinocoNode cmdPeek = (CommandOrinocoNode) peek.getNode();
					if (cmdPeek.getCommand() == ops.L_CURLY_BRACE) {
						error = true;
					}
				}

				if (error) {
					delayEvalStack.peek().getLastItemList().add(new InvalidTokenOrinocoNode(new CommandOrinocoNode(command)));
				} else {
					delayEvalStack.peek().getLastItemList().add(new CommandOrinocoNode(command));
					delayEvalStack.peek().getItems().add(new ArrayList<>());
				}
				return;
			} else {
				delayEvalStack.peek().getLastItemList().add(new CommandOrinocoNode(command));
			}

			return;
		}


		if (command == ops.L_SQ_BRACKET) {
			delayEvalStack.add(new DelayEvalOrinocoNode(OrinocoNode.Flag.Array, new CommandOrinocoNode(command)));
			return;
		}

		if (command == ops.L_CURLY_BRACE) {
			delayEvalStack.add(new DelayEvalOrinocoNode(OrinocoNode.Flag.CodeBlock, new CommandOrinocoNode(command)));
			return;
		}

		if (command == ops.R_SQ_BRACKET || command == ops.R_CURLY_BRACE) {
			processNode(new InvalidTokenOrinocoNode(new CommandOrinocoNode(command)));
			return;
		}

		if (command == ops.QUEST) {
			// todo https://community.bistudio.com/wiki/a_:_b
		}

		if (command == ops.COMMA || command == ops.COLON) {
			// todo report error
			// 1 + 1
			// acceptBinaryExpression(leftArgument, rightArgument, operator)
			// acceptUnaryExpression(rightArgument, operator)
			// acceptNularExpression(operator)

			// !a && b
			// a ! b &&
		}

		if (command == ops.SEMICOLON) {
			processNode(new CommandOrinocoNode(command));
			endExpression();
			return;
		}

		if (command == ops.LPAREN) {
			operators.add(command);
		} else if (command == ops.RPAREN) {
			while (!operators.isEmpty()) {
				Command peek = operators.peek();
				if (peek == ops.LPAREN) {
					break;
				}
				processNode(new CommandOrinocoNode(operators.pop()));
			}
			if (!operators.isEmpty()) {
				Command peek = operators.peek();
				if (peek != ops.LPAREN) {
					processNode(new InvalidTokenOrinocoNode(new CommandOrinocoNode(operators.pop())));
				} else {
					operators.pop();
				}
			}
		} else { // operator is encountered
			int prec = precedence(command);
			while (!operators.isEmpty() && prec <= precedence(operators.peek())) {
				if (operators.peek() == ops.LPAREN) {
					processNode(new InvalidTokenOrinocoNode(new CommandOrinocoNode(operators.pop())));
				} else {
					processNode(new CommandOrinocoNode(operators.pop()));
				}
			}
		}

		operators.add(command);
	}

	private void processOrinocoToken(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		for (InfixPatternMatcher matcher : matchers) {
			matcher.acceptToken(token, ctx);
		}
	}

	@NotNull
	private ValueType getTypeForNode(@NotNull OrinocoNode node) {
		switch (node.getFlag()) {
			case Variable: {
				return ValueType.BaseType._VARIABLE;
			}
			case Literal: {
				TokenOrinocoNode tokenNode = (TokenOrinocoNode) node;
				OrinocoSQFTokenType tokenType = (OrinocoSQFTokenType) tokenNode.getToken().getTokenType();
				if (tokenType == OrinocoSQFTokenType.LiteralNumber) {
					return ValueType.BaseType.NUMBER;
				}
				if (tokenType == OrinocoSQFTokenType.LiteralString) {
					return ValueType.BaseType.STRING;
				}
				// node has unhandled token type
				throw new IllegalStateException(tokenType.toString());
			}
			case Array: {
				DelayEvalOrinocoNode delayNode = (DelayEvalOrinocoNode) node;
				return null;
			}
			case CodeBlock: {
				DelayEvalOrinocoNode delayNode = (DelayEvalOrinocoNode) node;
				return null;
			}
			case InvalidToken: {
				// todo report error
				return ValueType.BaseType._ERROR;
			}
			case Command: {
				CommandOrinocoNode con = (CommandOrinocoNode) node;
				// todo
				break;
			}

		}
		return ValueType.BaseType._ERROR;
	}

	private void processNode(@NotNull OrinocoNode node) {
		// todo handle + - commands (do this in acceptCommand)


		switch (node.getFlag()) {
			case Variable: // fall
			case Literal: // fall
			case Array: // fall
			case CodeBlock: {
				if (this.leftNode == null) {
					this.leftNode = node;
					this.leftType = getTypeForNode(node);
				} else {
					this.rightNode = node;
					this.rightType = getTypeForNode(node);
				}
				return;
			}
			case InvalidToken: {
				// todo report error
				return;
			}
			case Command: {
				CommandOrinocoNode con = (CommandOrinocoNode) node;
				if (con.getCommand().isStrictlyNular()) {
					if (this.leftNode == null) {
						this.leftNode = node;
					} else {
						this.rightNode = node;
					}
					return;
				}
				this.op = con;
				break;
			}

		}

		@NotNull List<CommandSyntax> syntaxList = this.op.getCommand().getSyntaxList();
		for (CommandSyntax cs : syntaxList) {

		}
	}

	private void addNode(@NotNull OrinocoNode node) {
		if (delayEvalStack.isEmpty()) {
			processNode(node);
		} else {
			delayEvalStack.peek().getLastItemList().add(node);
		}
	}

	@Override
	public void acceptLocalVariable(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		processOrinocoToken(token, ctx);
		addNode(new TokenOrinocoNode(OrinocoNode.Flag.Variable, token));
	}

	@Override
	public void acceptGlobalVariable(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		processOrinocoToken(token, ctx);
		addNode(new TokenOrinocoNode(OrinocoNode.Flag.Variable, token));
	}

	@Override
	public void acceptLiteral(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		processOrinocoToken(token, ctx);
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
		endExpression();
	}

	private void endExpression() {
		finalizePostfixOutput();
		this.canBeUnary = false;
	}

	private void finalizePostfixOutput() {
		while (!delayEvalStack.isEmpty()) {
			DelayEvalOrinocoNode pop = delayEvalStack.pop();
			if (!delayEvalStack.isEmpty()) {
				delayEvalStack.peek().getLastItemList().add(pop);
			} else {
				processNode(new InvalidTokenOrinocoNode(pop));
			}
		}
		while (!operators.isEmpty()) {
			processNode(new CommandOrinocoNode(operators.pop()));
		}
	}

	@Override
	public void reset() {
		this.canBeUnary = true;
		this.delayEvalStack.clear();
		this.leftNode = null;
		this.rightNode = null;
		this.op = null;
	}

	@NotNull
	public List<InfixPatternMatcher> getMatchers() {
		return matchers;
	}
}
