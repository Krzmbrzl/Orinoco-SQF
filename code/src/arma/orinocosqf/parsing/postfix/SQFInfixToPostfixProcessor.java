package arma.orinocosqf.parsing.postfix;

import arma.orinocosqf.Command;
import arma.orinocosqf.OrinocoSQFTokenType;
import arma.orinocosqf.OrinocoToken;
import arma.orinocosqf.OrinocoTokenInstanceProcessor;
import arma.orinocosqf.lexer.OrinocoLexerContext;
import arma.orinocosqf.sqf.SQFCommand;
import arma.orinocosqf.sqf.SQFCommands;
import arma.orinocosqf.syntax.CommandSyntax;
import arma.orinocosqf.type.ExpandedValueType;
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
	private final List<InfixPatternMatcher> matchers = new ArrayList<>();
	private final Stack<ProcessContext> processStack = new Stack<>();

	public SQFInfixToPostfixProcessor() {
	}

	private int precedence(@NotNull ProcessContext ctx, @NotNull Command cmd) {
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
		if (ctx.canBeUnary && cmd.canBeUnary()) {
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
	public void acceptCommand(@NotNull OrinocoToken acceptedOrinocoToken, @NotNull OrinocoLexerContext lctx) {
		processOrinocoToken(acceptedOrinocoToken, lctx);

		SQFCommand command = SQFCommands.instance.getCommandInstanceById(acceptedOrinocoToken.getId());
		if (command == null) {
			throw new IllegalStateException();
		}

		final ProcessContext pctx = this.processStack.peek();

		final SQFCommands.Operators ops = SQFCommands.ops();

		CommandOrinocoNode acceptedTokenAsCommandNode = new CommandOrinocoNode(acceptedOrinocoToken, command);

		if (!pctx.delayEvalStack.isEmpty()) {
			if (command == ops.L_SQ_BRACKET) {
				pctx.delayEvalStack.add(new DelayEvalOrinocoNode(OrinocoNode.Flag.Array, acceptedTokenAsCommandNode));
				return;
			} else if (command == ops.R_SQ_BRACKET) {
				DelayEvalOrinocoNode delayPeek = pctx.delayEvalStack.peek();
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
					pctx.delayEvalStack.pop();
				}
				if (pctx.delayEvalStack.isEmpty()) {
					processNode(delayPeek);
				} else {
					pctx.delayEvalStack.peek().getLastItemList().add(delayPeek);
				}
				return;
			} else if (command == ops.COMMA) {
				DelayEvalOrinocoNode peek = pctx.delayEvalStack.peek();
				boolean error = false;
				if (peek.getNode() instanceof CommandOrinocoNode) {
					CommandOrinocoNode cmdPeek = (CommandOrinocoNode) peek.getNode();
					if (cmdPeek.getCommand() != ops.L_SQ_BRACKET) {
						error = true;
					}
				}
				if (error) {
					pctx.delayEvalStack.peek().getLastItemList().add(new InvalidTokenOrinocoNode(acceptedTokenAsCommandNode));
				} else {
					pctx.delayEvalStack.peek().getLastItemList().add(acceptedTokenAsCommandNode);
					pctx.delayEvalStack.peek().getItems().add(new ArrayList<>());
				}
				return;
			} else if (command == ops.L_CURLY_BRACE) {
				pctx.delayEvalStack.add(new DelayEvalOrinocoNode(OrinocoNode.Flag.CodeBlock, acceptedTokenAsCommandNode));
				return;
			} else if (command == ops.R_CURLY_BRACE) {
				DelayEvalOrinocoNode delayPeek = pctx.delayEvalStack.peek();
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
					pctx.delayEvalStack.pop();
				}
				if (pctx.delayEvalStack.isEmpty()) {
					processNode(delayPeek);
				} else {
					pctx.delayEvalStack.peek().getLastItemList().add(delayPeek);
				}
				return;
			} else if (command == ops.SEMICOLON) {
				DelayEvalOrinocoNode peek = pctx.delayEvalStack.peek();
				boolean error = false;
				if (peek.getNode() instanceof CommandOrinocoNode) {
					CommandOrinocoNode cmdPeek = (CommandOrinocoNode) peek.getNode();
					if (cmdPeek.getCommand() == ops.L_CURLY_BRACE) {
						error = true;
					}
				}

				if (error) {
					pctx.delayEvalStack.peek().getLastItemList().add(new InvalidTokenOrinocoNode(acceptedTokenAsCommandNode));
				} else {
					pctx.delayEvalStack.peek().getLastItemList().add(acceptedTokenAsCommandNode);
					pctx.delayEvalStack.peek().getItems().add(new ArrayList<>());
				}
				return;
			} else {
				pctx.delayEvalStack.peek().getLastItemList().add(acceptedTokenAsCommandNode);
			}

			return;
		}


		if (command == ops.L_SQ_BRACKET) {
			pctx.delayEvalStack.add(new DelayEvalOrinocoNode(OrinocoNode.Flag.Array, acceptedTokenAsCommandNode));
			return;
		}

		if (command == ops.L_CURLY_BRACE) {
			pctx.delayEvalStack.add(new DelayEvalOrinocoNode(OrinocoNode.Flag.CodeBlock, acceptedTokenAsCommandNode));
			return;
		}

		if (command == ops.R_SQ_BRACKET || command == ops.R_CURLY_BRACE) {
			processNode(new InvalidTokenOrinocoNode(acceptedTokenAsCommandNode));
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
			processNode(acceptedTokenAsCommandNode);
			endExpression();
			return;
		}

		if (command == ops.LPAREN) {
			pctx.operators.add(acceptedOrinocoToken);
		} else if (command == ops.RPAREN) {
			while (!pctx.operators.isEmpty()) {
				Command peek = pctx.commandFromOperators(false);
				if (peek == ops.LPAREN) {
					break;
				}
				processNode(pctx.commandNodeFromOperators(true));
			}
			if (!pctx.operators.isEmpty()) {
				Command peek = pctx.commandFromOperators(false);
				if (peek != ops.LPAREN) {
					processNode(new InvalidTokenOrinocoNode(pctx.commandNodeFromOperators(true)));
				} else {
					pctx.operators.pop();
				}
			}
		} else { // operator is encountered
			int prec = precedence(pctx, command);
			while (!pctx.operators.isEmpty() && prec <= precedence(pctx, pctx.commandFromOperators(false))) {
				if (pctx.commandFromOperators(false) == ops.LPAREN) {
					processNode(new InvalidTokenOrinocoNode(pctx.commandNodeFromOperators(true)));
				} else {
					processNode(pctx.commandNodeFromOperators(true));
				}
			}
		}

		pctx.operators.add(acceptedOrinocoToken);
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
				ExpandedValueType type = new ExpandedValueType();

				for (List<OrinocoNode> list : delayNode.getItems()) {
					// todo handle trailing comma
					boolean invalid = false;
					for (OrinocoNode listNode : list) {
						if (listNode instanceof InvalidTokenOrinocoNode) {
							invalid = true;
							break;
						}
					}
					if (invalid) {
						type.getValueTypes().add(ValueType.BaseType._ERROR);
						continue;
					}
					this.processStack.push(new ProcessContext());
					for (OrinocoNode listNode : list) {
						this.processNode(listNode);
					}
					type.addValueType(this.processStack.pop().returnType);

				}
				return type;
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
		ProcessContext ctx = this.processStack.peek();

		switch (node.getFlag()) {
			case Variable: // fall
			case Literal: // fall
			case Array: // fall
			case CodeBlock: {
				if (ctx.leftNode == null) {
					ctx.leftNode = node;
					ctx.leftType = getTypeForNode(node);
				} else {
					ctx.rightNode = node;
					ctx.rightType = getTypeForNode(node);
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
					if (ctx.leftNode == null) {
						ctx.leftNode = node;
					} else {
						ctx.rightNode = node;
					}
					return;
				}
				ctx.op = con;
				break;
			}

		}

		@NotNull List<CommandSyntax> syntaxList = ctx.op.getCommand().getSyntaxList();
		for (CommandSyntax cs : syntaxList) {

		}
	}

	private void addNode(@NotNull OrinocoNode node) {
		ProcessContext ctx = this.processStack.peek();
		if (ctx.delayEvalStack.isEmpty()) {
			processNode(node);
		} else {
			ctx.delayEvalStack.peek().getLastItemList().add(node);
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

	private void acceptOrinocoNode(@NotNull OrinocoNode node, @NotNull OrinocoLexerContext ctx) {
		switch (node.getFlag()) {
			case Variable: {
				TokenOrinocoNode tokenNode = (TokenOrinocoNode) node;
				processOrinocoToken(tokenNode.getToken(), ctx);
				addNode(node);
				break;
			}
			case Command: {
				CommandOrinocoNode commandNode = (CommandOrinocoNode) node;
				processOrinocoToken(commandNode.getToken(), ctx);
				break;
			}
			default: {
				throw new IllegalStateException(); // should have handled all cases
			}
		}
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
		ProcessContext ctx = this.processStack.peek();
		finalizePostfixOutput();
		ctx.canBeUnary = false;
	}

	private void finalizePostfixOutput() {
		ProcessContext ctx = this.processStack.peek();
		while (!ctx.delayEvalStack.isEmpty()) {
			DelayEvalOrinocoNode pop = ctx.delayEvalStack.pop();
			if (!ctx.delayEvalStack.isEmpty()) {
				ctx.delayEvalStack.peek().getLastItemList().add(pop);
			} else {
				processNode(new InvalidTokenOrinocoNode(pop));
			}
		}
		while (!ctx.operators.isEmpty()) {
			processNode(ctx.commandNodeFromOperators(true));
		}
	}

	@Override
	public void reset() {
		this.processStack.clear();
		this.processStack.push(new ProcessContext());
	}

	@NotNull
	public List<InfixPatternMatcher> getMatchers() {
		return matchers;
	}

	private static class ProcessContext {
		boolean canBeUnary = false;
		final Stack<OrinocoToken> operators = new Stack<>();
		final Stack<DelayEvalOrinocoNode> delayEvalStack = new Stack<>();
		OrinocoNode leftNode, rightNode;
		ValueType leftType, rightType, returnType;
		CommandOrinocoNode op;

		@NotNull
		public Command commandFromOperators(boolean pop) {
			int id = operators.peek().getId();
			if (pop) {
				operators.pop();
			}
			SQFCommand commandInstanceById = SQFCommands.instance.getCommandInstanceById(id);
			if (commandInstanceById == null) {
				throw new IllegalStateException(); // ?
			}
			return commandInstanceById;
		}

		@NotNull
		public CommandOrinocoNode commandNodeFromOperators(boolean pop) {
			OrinocoToken token = operators.peek();
			int id = token.getId();
			if (pop) {
				operators.pop();
			}
			SQFCommand commandInstanceById = SQFCommands.instance.getCommandInstanceById(id);
			if (commandInstanceById == null) {
				throw new IllegalStateException(); // ?
			}
			return new CommandOrinocoNode(token, commandInstanceById);
		}
	}
}
