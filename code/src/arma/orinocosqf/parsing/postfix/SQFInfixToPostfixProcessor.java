package arma.orinocosqf.parsing.postfix;

import arma.orinocosqf.OrinocoSQFTokenType;
import arma.orinocosqf.OrinocoToken;
import arma.orinocosqf.OrinocoTokenInstanceProcessor;
import arma.orinocosqf.lexer.OrinocoLexerContext;
import arma.orinocosqf.problems.Problem;
import arma.orinocosqf.problems.ProblemListener;
import arma.orinocosqf.problems.Problems;
import arma.orinocosqf.sqf.SQFCommand;
import arma.orinocosqf.sqf.SQFCommandSyntax;
import arma.orinocosqf.sqf.SQFCommands;
import arma.orinocosqf.type.ExpandedValueType;
import arma.orinocosqf.type.ValueType;
import arma.orinocosqf.type.ValueType.BaseType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static arma.orinocosqf.util.ASCIITextHelper.equalsIgnoreCase;

/**
 * @author K
 * @since 7/28/19
 */
public class SQFInfixToPostfixProcessor implements OrinocoTokenInstanceProcessor, ProblemListener {
	private final List<InfixPatternMatcher> matchers = new ArrayList<>();
	private final Stack<ProcessContext> processStack = new Stack<>();
	private ProblemListener problemListener;

	public SQFInfixToPostfixProcessor() {
	}

	private int precedence(@NotNull ProcessContext ctx, @NotNull OrinocoToken token, @NotNull SQFCommand cmd) {
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
		if (ctx.secondType == null && cmd.canBeUnary()) {
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
		illegalCharacterProblem(token, "Expected a binary command/operator");
		return 8;
	}

	@Override
	public void acceptCommand(@NotNull OrinocoToken orinocoToken, @NotNull OrinocoLexerContext lctx) {
		for (InfixPatternMatcher matcher : matchers) {
			matcher.acceptToken(orinocoToken, lctx);
		}

		SQFCommand command = SQFCommands.instance.getCommandInstanceById(orinocoToken.getId());
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
			if (!pctx.inspiredBy.inspirationEquals(ProcessContextInspiration.array())) {
				illegalCharacterProblem(orinocoToken, "] is not allowed here");
			} else {
				this.processStack.pop();
				boolean success = processNularType(pctx.returnType);
				if (!success) {
					int off = orinocoToken.getOriginalOffset();
					int len = orinocoToken.getOriginalLength();
					int line = 0;
					// todo highlight whole array value not just closing ]
					this.problemEncountered(Problems.ERROR_SYNTAX_TOO_MANY_OPERANDS, "Array value not allowed here", off, len, line);
				}
			}
			return;
		} else if (command == ops.COMMA) {
			if (!pctx.inspiredBy.inspirationEquals(ProcessContextInspiration.array())) {
				illegalCharacterProblem(orinocoToken, ", is not allowed here");
			} else {
				pctx.returnType.getExpanded().addValueType(pctx.firstType);
				pctx.firstType = null;
				pctx.secondType = null;
			}
			return;
		} else if (command == ops.L_CURLY_BRACE) {
			ProcessContext codeContext = new ProcessContext("code", ProcessContextInspiration.code());
			codeContext.returnType = BaseType.NOTHING;
			this.processStack.push(codeContext);
			return;
		} else if (command == ops.R_CURLY_BRACE) {
			if (!pctx.inspiredBy.inspirationEquals(ProcessContextInspiration.code())) {
				illegalCharacterProblem(orinocoToken, "} is not allowed here");
			} else {
				this.processStack.pop();
				boolean success = processNularType(pctx.firstType == null ? BaseType.NOTHING : pctx.firstType);
				if (!success) {
					int off = orinocoToken.getOriginalOffset();
					int len = orinocoToken.getOriginalLength();
					int line = 0;
					// todo highlight whole array value not just closing }
					this.problemEncountered(Problems.ERROR_SYNTAX_TOO_MANY_OPERANDS, "Code block not allowed here", off, len, line);
				}
			}
			return;
		} else if (command == ops.SEMICOLON) {
			pctx.returnType = null;
			pctx.firstType = null;
			pctx.secondType = null;
			return;
		}


		if (command == ops.QUEST) {
			// todo https://community.bistudio.com/wiki/a_:_b
		}

		if (command == ops.COLON) {
			// todo
		}

		if (command == ops.LPAREN) {
			pctx.operators.add(orinocoToken);
		} else if (command == ops.RPAREN) {
			while (!pctx.operators.isEmpty()) {
				SQFCommand peek = pctx.commandFromOperators(false);
				if (peek == ops.LPAREN) {
					break;
				}
				processCommand(pctx.peekOperatorToken(), pctx.commandFromOperators(true));
			}
			if (!pctx.operators.isEmpty()) {
				SQFCommand peek = pctx.commandFromOperators(false);
				if (peek != ops.LPAREN) {
					illegalCharacterProblem(pctx.peekOperatorToken(), "Expected a ( here");
					processCommand(pctx.peekOperatorToken(), pctx.commandFromOperators(true));
				} else {
					pctx.operators.pop();
				}
			}
		} else {
			if (command.isStrictlyNular()) {
				boolean success = processNularType(command.getSyntaxList().get(0).getReturnValue().getType());
				if (!success) {
					illegalCharacterProblem(orinocoToken, "Command " + command.getCommandName() + " is not allowed here");
				}
				return;
			}

			// operator is encountered
			int prec = precedence(pctx, orinocoToken, command);
			while (!pctx.operators.isEmpty() && prec <= precedence(pctx, pctx.peekOperatorToken(), pctx.commandFromOperators(false))) {
				if (pctx.commandFromOperators(false) == ops.LPAREN) {
					illegalCharacterProblem(pctx.peekOperatorToken(), "( is not allowed here");
					processCommand(pctx.peekOperatorToken(), pctx.commandFromOperators(true));
				} else {
					processCommand(pctx.peekOperatorToken(), pctx.commandFromOperators(true));
				}
			}
		}

		pctx.operators.add(orinocoToken);
	}

	private void processCommand(@NotNull OrinocoToken token, @NotNull SQFCommand cmd) {
		ProcessContext pctx = this.processStack.peek();
		if (pctx.firstType == null) {
			String msg = "Command " + cmd.getCommandName() + " expects an operand and got none";
			this.tokenProblem(token, Problems.ERROR_SYNTAX_TOO_MANY_OPERANDS, msg);
			return;
		}
		if (pctx.secondType == null) {
			if (!cmd.canBeUnary()) {
				String msg = "Command " + cmd.getCommandName() + " expects 2 operands";
				this.tokenProblem(token, Problems.ERROR_SYNTAX_TOO_MANY_OPERANDS, msg);
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
				String msg = String.format("Command %1$s has no syntax for %1$s %2$s", cmd.getCommandName(), pctx.firstType);
				this.tokenProblem(token, Problems.ERROR_INVALID_COMMAND_SYNTAX, msg);
			} else {
				if (pctx.firstType.isHardEqual(BaseType._VARIABLE)) {
					// set to _VARIABLE to preserve uncertainty about return type
					commandReturnType = BaseType._VARIABLE;
				}
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
					break;
				}
			}
		}
		if (commandReturnType == null) {
			String f = "Command %1$s has no syntax for %2$s %1$s %3$s";
			String msg = String.format(f, cmd.getCommandName(), pctx.firstType.getDisplayName(), pctx.secondType.getDisplayName());
			this.tokenProblem(token, Problems.ERROR_INVALID_COMMAND_SYNTAX, msg);
		} else {
			if (pctx.firstType.isHardEqual(BaseType._VARIABLE) || pctx.secondType.isHardEqual(BaseType._VARIABLE)) {
				// set to _VARIABLE to preserve uncertainty about return type
				commandReturnType = BaseType._VARIABLE;
			}
			pctx.firstType = commandReturnType;
			pctx.secondType = null;
		}
	}

	private void illegalCharacterProblem(@NotNull OrinocoToken orinocoToken, String s) {
		this.tokenProblem(orinocoToken, Problems.ERROR_SYNTAX_ILLEGAL_CHARACTER, s);
	}

	private void tokenProblem(@NotNull OrinocoToken orinocoToken, @NotNull Problem p, String s) {
		int off = orinocoToken.getOriginalOffset();
		int len = orinocoToken.getOriginalLength();
		int line = 0;
		this.problemEncountered(p, s, off, len, line);
	}


	private boolean processNularType(@NotNull ValueType valueType) {
		ProcessContext pctx = this.processStack.peek();
		if (pctx.firstType == null) {
			pctx.firstType = valueType;
			return true;
		} else if (pctx.secondType == null) {
			pctx.secondType = valueType;
			return true;
		}
		return false;
	}

	@Override
	public void acceptLocalVariable(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		for (InfixPatternMatcher matcher : matchers) {
			matcher.acceptToken(token, ctx);
		}
		boolean success = processNularType(BaseType._VARIABLE);
		if (!success) {
			illegalCharacterProblem(token, "variable is not allowed here");
		}
	}

	@Override
	public void acceptGlobalVariable(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		for (InfixPatternMatcher matcher : matchers) {
			matcher.acceptToken(token, ctx);
		}

		boolean success = processNularType(BaseType._VARIABLE);
		if (!success) {
			illegalCharacterProblem(token, "variable is not allowed here");
		}
	}

	@Override
	public void acceptLiteral(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		for (InfixPatternMatcher matcher : matchers) {
			matcher.acceptToken(token, ctx);
		}
		OrinocoSQFTokenType tokenType = (OrinocoSQFTokenType) token.getTokenType();
		ValueType valueType = null;
		String type = "";
		if (tokenType == OrinocoSQFTokenType.LiteralNumber) {
			valueType = BaseType.NUMBER;
			type = "number";
		} else if (tokenType == OrinocoSQFTokenType.LiteralString) {
			valueType = BaseType.STRING;
			type = "string";
		} else {
			throw new IllegalStateException();
		}

		boolean success = processNularType(valueType);
		if (!success) {
			illegalCharacterProblem(token, type + " is not allowed here");
		}
	}

	private static class ProcessContext {

		final Stack<OrinocoToken> operators = new Stack<>();
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
		public OrinocoToken peekOperatorToken() {
			return operators.peek();
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
		while (!this.processStack.isEmpty()) {
			ProcessContext pctx = this.processStack.peek();
			while (!pctx.operators.isEmpty()) {
				processCommand(pctx.peekOperatorToken(), pctx.commandFromOperators(true));
			}
			this.processStack.pop();
		}
	}

	@Override
	public void begin(@NotNull OrinocoLexerContext ctx) {
		reset();
	}

	@Nullable
	public ProblemListener getProblemListener() {
		return problemListener;
	}

	public void setProblemListener(@NotNull ProblemListener problemListener) {
		this.problemListener = problemListener;
	}

	@Override
	public void problemEncountered(@NotNull Problem problem, @NotNull String msg, int offset, int length, int line) {
		if (this.problemListener != null) {
			this.problemListener.problemEncountered(problem, msg, offset, length, line);
		}
	}

}
