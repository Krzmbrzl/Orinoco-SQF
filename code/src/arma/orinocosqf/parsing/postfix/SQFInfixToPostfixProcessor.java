package arma.orinocosqf.parsing.postfix;

import arma.orinocosqf.OrinocoSQFTokenType;
import arma.orinocosqf.OrinocoToken;
import arma.orinocosqf.OrinocoTokenInstanceProcessor;
import arma.orinocosqf.lexer.OrinocoLexerContext;
import arma.orinocosqf.sqf.SQFCommand;
import arma.orinocosqf.sqf.SQFCommandSyntax;
import arma.orinocosqf.sqf.SQFCommands;
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

	private int precedence(@NotNull ProcessContext ctx, @NotNull SQFCommand cmd) {
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
	public void acceptCommand(@NotNull OrinocoToken acceptedOrinocoToken, @NotNull OrinocoLexerContext lctx) {
		for (InfixPatternMatcher matcher : matchers) {
			matcher.acceptToken(acceptedOrinocoToken, lctx);
		}

		SQFCommand command = SQFCommands.instance.getCommandInstanceById(acceptedOrinocoToken.getId());
		if (command == null) {
			throw new IllegalStateException();
		}

		final ProcessContext pctx = this.processStack.peek();

		final SQFCommands.Operators ops = SQFCommands.ops();

		if (command == ops.L_SQ_BRACKET) {
			ProcessContext arrayContext = new ProcessContext("array", ProcessContextInspiration.array());
			arrayContext.returnType = new ExpandedValueType();
			this.processStack.push(arrayContext);
			return;
		} else if (command == ops.R_SQ_BRACKET) {
			if (pctx.inspiredBy.inspirationEquals(ProcessContextInspiration.array())) {
				// todo report error
			} else {
				this.processStack.pop();
				ProcessContext peek = this.processStack.peek();
				processNularType(pctx.returnType);
			}
			return;
		} else if (command == ops.COMMA) {
			if (pctx.inspiredBy.inspirationEquals(ProcessContextInspiration.array())) {
				// todo report error
			} else {
				pctx.returnType.getExpanded().addValueType(pctx.firstType);
				pctx.firstType = null;
				pctx.secondType = null;
			}
			return;
		} else if (command == ops.L_CURLY_BRACE) {
			ProcessContext codeContext = new ProcessContext("code", ProcessContextInspiration.code());
			codeContext.returnType = ValueType.BaseType.NOTHING;
			this.processStack.push(codeContext);
			return;
		} else if (command == ops.R_CURLY_BRACE) {
			if (pctx.inspiredBy.inspirationEquals(ProcessContextInspiration.code())) {
				// todo report error
			} else {
				this.processStack.pop();
				ProcessContext peek = this.processStack.peek();
				processNularType(pctx.returnType);
			}
			return;
		} else if (command == ops.SEMICOLON) {
			pctx.returnType = pctx.firstType;
			pctx.firstType = null;
			pctx.secondType = null;
			return;
		}


		if (command == ops.QUEST) {
			// todo https://community.bistudio.com/wiki/a_:_b
		}

		if (command == ops.COLON) {
			// todo report error
			// 1 + 1
			// acceptBinaryExpression(leftArgument, rightArgument, operator)
			// acceptUnaryExpression(rightArgument, operator)
			// acceptNularExpression(operator)

			// !a && b
			// a ! b &&
		}

		if (command == ops.LPAREN) {
			pctx.operators.add(acceptedOrinocoToken);
		} else if (command == ops.RPAREN) {
			while (!pctx.operators.isEmpty()) {
				SQFCommand peek = pctx.commandFromOperators(false);
				if (peek == ops.LPAREN) {
					break;
				}
				processCommand(pctx.commandFromOperators(true));
			}
			if (!pctx.operators.isEmpty()) {
				SQFCommand peek = pctx.commandFromOperators(false);
				if (peek != ops.LPAREN) {
					// todo report error
					processCommand(pctx.commandFromOperators(true));
				} else {
					pctx.operators.pop();
				}
			}
		} else { // operator is encountered
			int prec = precedence(pctx, command);
			while (!pctx.operators.isEmpty() && prec <= precedence(pctx, pctx.commandFromOperators(false))) {
				if (pctx.commandFromOperators(false) == ops.LPAREN) {
					// todo report error
					processCommand(pctx.commandFromOperators(true));
				} else {
					processCommand(pctx.commandFromOperators(true));
				}
			}
		}

		pctx.operators.add(acceptedOrinocoToken);
	}

	private void processCommand(@NotNull SQFCommand cmd) {
		if (cmd.isStrictlyNular()) {
			processNularType(cmd.getSyntaxList().get(0).getReturnValue().getType());
			return;
		}
		ProcessContext pctx = this.processStack.peek();
		if (pctx.firstType == null) {
			// todo report error not enough operands
			return;
		}
		if (pctx.secondType == null) {
			if (!cmd.canBeUnary()) {
				//todo report error too few operands
				return;
			}
			// unary command
			ValueType commandReturnType = null;
			for (SQFCommandSyntax syntax : cmd.getSyntaxList()) {
				if (syntax.getRightParam() == null || syntax.getLeftParam() != null) {
					continue;
				}
				if (syntax.getRightParam().getType().typeEquivalent(pctx.firstType)) {
					commandReturnType = syntax.getReturnValue().getType();
					break;
				}
			}
			if (commandReturnType == null) {
				//todo report error no syntax available
			} else {
				pctx.firstType = commandReturnType;
				pctx.secondType = null;
			}
			return;
		}

		// binary command
		ValueType commandReturnType = null;
		for (SQFCommandSyntax syntax : cmd.getSyntaxList()) {
			if (syntax.getLeftParam() == null || syntax.getRightParam() == null) {
				continue;
			}
			if (syntax.getLeftParam().getType().typeEquivalent(pctx.firstType)) {
				if (syntax.getRightParam().getType().typeEquivalent(pctx.secondType)) {
					commandReturnType = syntax.getReturnValue().getType();
				}
				break;
			}
		}
		if (commandReturnType == null) {
			//todo report error no syntax available
		} else {
			pctx.firstType = commandReturnType;
			pctx.secondType = null;
		}
	}


	private void processNularType(@NotNull ValueType valueType) {
		ProcessContext pctx = this.processStack.peek();
		if (pctx.firstType == null) {
			pctx.firstType = valueType;
		} else if (pctx.secondType == null) {
			pctx.secondType = valueType;
		} else {
			// todo report error expected operator
		}
	}

	@Override
	public void acceptLocalVariable(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		for (InfixPatternMatcher matcher : matchers) {
			matcher.acceptToken(token, ctx);
		}
		processNularType(ValueType.BaseType._VARIABLE);
	}

	@Override
	public void acceptGlobalVariable(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		for (InfixPatternMatcher matcher : matchers) {
			matcher.acceptToken(token, ctx);
		}

		processNularType(ValueType.BaseType._VARIABLE);
	}

	@Override
	public void acceptLiteral(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		for (InfixPatternMatcher matcher : matchers) {
			matcher.acceptToken(token, ctx);
		}
		OrinocoSQFTokenType tokenType = (OrinocoSQFTokenType) token.getTokenType();
		ProcessContext pctx = this.processStack.peek();
		ValueType valueType = null;
		if (tokenType == OrinocoSQFTokenType.LiteralNumber) {
			valueType = ValueType.BaseType.NUMBER;
		} else if (tokenType == OrinocoSQFTokenType.LiteralString) {
			valueType = ValueType.BaseType.STRING;
		} else {
			throw new IllegalStateException();
		}

		processNularType(valueType);
	}

	private static class ProcessContext {
		boolean canBeUnary = false;
		final Stack<OrinocoToken> operators = new Stack<>();
		OrinocoToken leftToken, rightToken;
		ValueType firstType, secondType, returnType;
		final String debugName;
		/** What inspired the creation for the context */
		@NotNull
		final ProcessContextInspiration inspiredBy;

		public ProcessContext(@NotNull String debugName, @NotNull ProcessContextInspiration inspiredBy) {
			this.debugName = debugName;
			this.inspiredBy = inspiredBy;
		}

		@NotNull
		public SQFCommand commandFromOperators(boolean pop) {
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

	}

	@Override
	public void reset() {
		this.processStack.clear();
		this.processStack.push(new ProcessContext("root", ProcessContextInspiration.root()));
	}

	@NotNull
	public List<InfixPatternMatcher> getMatchers() {
		return matchers;
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
		// endExpression();
	}

	@Override
	public void begin(@NotNull OrinocoLexerContext ctx) {
		reset();
	}

}
