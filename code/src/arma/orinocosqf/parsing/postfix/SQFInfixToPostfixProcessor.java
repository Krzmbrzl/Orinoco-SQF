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
import arma.orinocosqf.type.CodeType;
import arma.orinocosqf.type.ExpandedValueType;
import arma.orinocosqf.type.SingletonArrayExpandedValueType;
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
	@NotNull
	private ValueType returnType = BaseType.NOTHING;

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
		final SQFCommands.Operators ops = SQFCommands.ops();
		if (cmd == ops.HASH) {
			return 3;
		}
		if (cmd == ops.CARET) {
			return 4;
		}
		final String cn = cmd.getName();
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
		illegalTokenProblem(token, "Expected a binary command/operator");
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
			arrayContext.helperType = new ExpandedValueType();
			this.processStack.push(arrayContext);
			return;
		} else if (command == ops.R_SQ_BRACKET) {
			if (!pctx.inspiredBy.inspirationEquals(ProcessContextInspiration.array())) {
				illegalTokenProblem(orinocoToken, "] is not allowed here");
			} else {
				this.finishExpression();
				if (pctx.firstType != null) {  // if null, then it's an empty array
					pctx.helperType.getExpanded().addValueType(pctx.firstType);
				}
				final int arrayLength = pctx.helperType.getExpanded().getValueTypes().size();
				if (arrayLength == 0) {
					pctx.helperType = new ExpandedValueType(); //empty expanded type is assumed empty array
				} else if (arrayLength == 1) {
					// need to use singleton array expanded value type because expanded type with one value
					// is not assumed to be an array
					pctx.helperType = new SingletonArrayExpandedValueType(pctx.helperType.getExpanded().getValueTypes().get(0));
				}

				this.processStack.pop();
				boolean success = processNularType(pctx.helperType);
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
				illegalTokenProblem(orinocoToken, ", is not allowed here");
			} else {
				pctx.helperType.getExpanded().addValueType(pctx.firstType);
				pctx.firstType = null;
				pctx.secondType = null;
			}
			return;
		} else if (command == ops.L_CURLY_BRACE) {
			ProcessContext codeContext = new ProcessContext("code", ProcessContextInspiration.code());
			this.processStack.push(codeContext);
			return;
		} else if (command == ops.R_CURLY_BRACE) {
			if (!pctx.inspiredBy.inspirationEquals(ProcessContextInspiration.code())) {
				illegalTokenProblem(orinocoToken, "} is not allowed here");
			} else {
				this.processStack.pop();
				CodeType codeType = pctx.firstType == null ? new CodeType(BaseType.NOTHING) : new CodeType(pctx.firstType);
				boolean success = processNularType(codeType);
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
			if (pctx.firstType != null && pctx.secondType != null && pctx.operators.isEmpty()) {
				this.illegalTokenProblem(pctx.lastToken, "Token not allowed here. Expected ;");
			} else {
				this.finishExpression();
			}

			pctx.helperType = null;
			pctx.firstType = null;
			pctx.secondType = null;
			return;
		}


		if (command == ops.QUEST) {
			// todo https://community.bistudio.com/wiki/a_:_b
		}

		if (command == ops.LPAREN) {
			pctx.operators.add(orinocoToken);
			return;
		}
		if (command == ops.RPAREN) {
			while (!pctx.operators.isEmpty()) {
				SQFCommand peek = pctx.cmdFromOps(false);
				if (peek == ops.LPAREN) {
					break;
				}
				processNonNularCommand(pctx.peekOpToken(), pctx.cmdFromOps(true));
			}
			if (!pctx.operators.isEmpty()) {
				SQFCommand peek = pctx.cmdFromOps(false);
				if (peek != ops.LPAREN) {
					illegalTokenProblem(pctx.peekOpToken(), "Expected a ( here");
					processNonNularCommand(pctx.peekOpToken(), pctx.cmdFromOps(true));
				} else {
					pctx.cmdFromOps(true);
				}
			}
			return;
		}

		if (command.isStrictlyNular()) {
			pctx.lastToken = orinocoToken;
			boolean success = processNularType(command.getSyntaxList().get(0).getReturnValue().getType());
			if (!success) {
				illegalTokenProblem(orinocoToken, "Command " + command.getCommandName() + " is not allowed here");
			}
			return;
		}

		// operator is encountered
		int prec = precedence(pctx, orinocoToken, command);
		while (!pctx.operators.isEmpty() && prec <= precedence(pctx, pctx.peekOpToken(), pctx.cmdFromOps(false))) {
			if (pctx.cmdFromOps(false) == ops.LPAREN) {
				illegalTokenProblem(pctx.peekOpToken(), "( is not allowed here");
				pctx.cmdFromOps(true);
				continue;
			}
			processNonNularCommand(pctx.peekOpToken(), pctx.cmdFromOps(true));
		}


		pctx.operators.add(orinocoToken);
	}

	private void processNonNularCommand(@NotNull OrinocoToken token, @NotNull SQFCommand cmd) {
		ProcessContext pctx = this.processStack.peek();
		pctx.lastToken = token;
		if (pctx.firstType == null) {
			String msg = "Command " + cmd.getCommandName() + " expects an operand and got none";
			this.tokenProblem(token, Problems.ERROR_SYNTAX_TOO_FEW_OPERANDS, msg);
			return;
		}
		if (pctx.secondType == null) {
			if (!cmd.canBeUnary()) {
				String msg = "Command " + cmd.getCommandName() + " expects 2 operands";
				this.tokenProblem(token, Problems.ERROR_SYNTAX_TOO_FEW_OPERANDS, msg);
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

	private void illegalTokenProblem(@NotNull OrinocoToken orinocoToken, String s) {
		this.tokenProblem(orinocoToken, Problems.ERROR_SYNTAX_ILLEGAL_TOKEN, s);
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

			if (!pctx.operators.isEmpty()) {
				SQFCommand peekCmd = pctx.cmdFromOps(false);
				if (peekCmd.canBeUnary()) {
					processNonNularCommand(pctx.peekOpToken(), pctx.cmdFromOps(true));
				}
			}

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
		this.processStack.peek().lastToken = token;
		boolean success = processNularType(BaseType._VARIABLE);
		if (!success) {
			illegalTokenProblem(token, "variable is not allowed here");
		}
	}

	@Override
	public void acceptGlobalVariable(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		for (InfixPatternMatcher matcher : matchers) {
			matcher.acceptToken(token, ctx);
		}

		this.processStack.peek().lastToken = token;
		boolean success = processNularType(BaseType._VARIABLE);
		if (!success) {
			illegalTokenProblem(token, "variable is not allowed here");
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
		this.processStack.peek().lastToken = token;
		boolean success = processNularType(valueType);
		if (!success) {
			illegalTokenProblem(token, type + " is not allowed here");
		}
	}

	private static class ProcessContext {

		final Stack<OrinocoToken> operators = new Stack<>();
		ValueType firstType, secondType, helperType;
		final String debugName;
		/** What inspired the creation for the context */
		@NotNull
		final ProcessContextInspiration inspiredBy;
		OrinocoToken lastToken;

		public ProcessContext(@NotNull String debugName, @NotNull ProcessContextInspiration inspiredBy) {
			this.debugName = debugName;
			this.inspiredBy = inspiredBy;
		}

		@NotNull
		public OrinocoToken peekOpToken() {
			return operators.peek();
		}

		@NotNull
		public SQFCommand cmdFromOps(boolean pop) {
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
		this.returnType = BaseType.NOTHING;
		while (!this.processStack.isEmpty()) {
			finishExpression();
			final ValueType peekFirst = this.processStack.peek().firstType;
			this.returnType = peekFirst == null ? BaseType.NOTHING : peekFirst;
			this.processStack.pop();
		}
	}

	private void finishExpression() {
		ProcessContext pctx = this.processStack.peek();
		while (!pctx.operators.isEmpty()) {
			processNonNularCommand(pctx.peekOpToken(), pctx.cmdFromOps(true));
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

	@NotNull
	public ValueType getReturnType() {
		return returnType;
	}
}
