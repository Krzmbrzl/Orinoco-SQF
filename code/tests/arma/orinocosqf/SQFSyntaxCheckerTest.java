package arma.orinocosqf;


import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.helpers.SQFSyntaxCheckerTestHelper;
import arma.orinocosqf.sqf.SQFCommand;
import arma.orinocosqf.sqf.SQFCommandSyntax;
import arma.orinocosqf.sqf.SQFCommands;
import arma.orinocosqf.syntax.ArrayParam;
import arma.orinocosqf.syntax.BIGame;
import arma.orinocosqf.syntax.Param;
import arma.orinocosqf.syntax.ReturnValueHolder;
import arma.orinocosqf.type.ExpandedValueType;
import arma.orinocosqf.type.ValueType;
import org.junit.Test;

import java.util.Arrays;

import static arma.orinocosqf.type.ValueType.BaseType;
import static org.junit.Assert.assertEquals;

/**
 * Tests for syntax/type checking for SQF code
 *
 * @author Kayler
 * @since 11/15/2017
 */
public class SQFSyntaxCheckerTest extends SQFSyntaxCheckerTestHelper {

	//----START Literal Expression----
	@Test
	public void testLiteralExpression_number() {
		ValueType ret = getExitTypeForText("1");
		assertEquals(ValueType.BaseType.NUMBER, ret);
	}

	@Test
	public void testLiteralExpression_string() {
		ValueType ret = getExitTypeForText("'hello'");
		assertEquals(ValueType.BaseType.STRING, ret);
	}

	@Test
	public void testLiteralExpression_array() {
		ValueType ret = getExitTypeForText("[1,2,3]");

		ValueType t = new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);

		assertEquals(t, ret);
	}

	//----END Literal Expression----

	//----START Paren Expression----

	@Test
	public void testParenExpression1() {
		ValueType ret = getExitTypeForText("(1)");
		assertEquals(BaseType.NUMBER, ret);
	}

	@Test
	public void testParenExpression2() {
		ValueType ret = getExitTypeForText("('hello')");
		assertEquals(BaseType.STRING, ret);
	}

	@Test
	public void testParenExpression3() {
		ValueType ret = getExitTypeForText("([1,2,3])");
		ValueType t = new ExpandedValueType(ValueType.BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);

		assertEquals(t, ret);
	}

	@Test
	public void testParenExpression4() {
		ValueType ret = getExitTypeForText("(1+1)");
		assertEquals(BaseType.NUMBER, ret);
	}

	//----END Paren Expression----

	//----START Add Expression----

	@Test
	public void testAddExpression_valid_number() {
		assertNoProblems("1+1");
	}

	@Test
	public void testAddExpression_valid_string() {
		assertNoProblems("''+''");
	}

	@Test
	public void testAddExpression_valid_array() {
		assertNoProblems("[]+[]");
	}

	@Test
	public void testAddExpression_valid_variable() {
		assertNoProblems("_var+_var");
		assertNoProblems("1+_var");
		assertNoProblems("[]+_var");
		assertNoProblems("''+_var");
		assertNoProblems("_var+1");
		assertNoProblems("_var+[]");
		assertNoProblems("_var+''");
		assertNoProblems("_var+_var+[driver _target]");
	}

	@Test
	public void testAddExpression_bad_numAndString() {
		assertHasProblems("1e1+''");
		assertHasProblems("''+1e1");
	}

	@Test
	public void testAddExpression_bad_numAndArray() {
		assertHasProblems("0.5+[]");
		assertHasProblems("[]+0.5");
	}

	@Test
	public void testAddExpression_bad_stringAndArray() {
		assertHasProblems("'hi'+[]");
		assertHasProblems("[]+'hello'");
	}

	//----END Add Expression----

	//----START Sub Expression----

	@Test
	public void testSubExpression_valid_number() {
		assertNoProblems("1-1");
	}

	@Test
	public void testSubExpression_valid_array() {
		assertNoProblems("[]-[]");
	}

	@Test
	public void testSubExpression_valid_variable() {
		assertNoProblems("_var-_var");
		assertNoProblems("1-_var");
		assertNoProblems("[]-_var");
		assertNoProblems("_var-1");
		assertNoProblems("_var-[]");
		assertNoProblems("_var-_var-[driver _target]");
	}

	@Test
	public void testSubExpression_bad_numAndString() {
		assertHasProblems("1e1-''");
		assertHasProblems("''-1e1");
	}

	@Test
	public void testSubExpression_bad_numAndArray() {
		assertHasProblems("0.5-[]");
		assertHasProblems("[]-0.5");
	}

	//----END Sub Expression----

	//----START Mult Expression----
	@Test
	public void testMultExpression_valid() {
		assertNoProblems("0*1.5");
	}

	@Test
	public void testMultExpression_bad() {
		assertHasProblems("0*[]");
		assertHasProblems("[]*0");
	}

	@Test
	public void testMultExpression_valid_variable() {
		assertNoProblems("_var*_var");
		assertNoProblems("1*_var");
		assertNoProblems("_var*1");
	}
	//----END Mult Expression----

	//----START Mod Expression----
	@Test
	public void testModExpression_valid() {
		assertNoProblems("0%1.5");
	}

	@Test
	public void testModExpression_bad() {
		assertHasProblems("0%[]");
		assertHasProblems("[]%0");
	}

	@Test
	public void testModExpression_valid_variable() {
		assertNoProblems("_var%_var");
		assertNoProblems("1%_var");
		assertNoProblems("_var%1");
	}
	//----END Mod Expression----

	//----START Div Expression----
	@Test
	public void testDivExpression_valid_number() {
		assertNoProblems("0/1.5");
	}

	@Test
	public void testDivExpression_valid_config() {
		assertNoProblems("configFile/'CfgVehicles'");
		assertNoProblems("_var/'test'");
	}

	@Test
	public void testDivExpression_bad_config() {
		assertHasProblems("'test'/_var");
		assertHasProblems("'test'/configFile");
		assertHasProblems("0/configFile");
		assertHasProblems("configFile/0");
	}

	@Test
	public void testDivExpression_bad_number() {
		assertHasProblems("0/[]");
		assertHasProblems("[]/0");
	}

	@Test
	public void testDivExpression_valid_number_variable() {
		assertNoProblems("_var/_var");
		assertNoProblems("1/_var");
		assertNoProblems("_var/1");
	}

	//----END Div Expression----

	//----START Bool And Expression----
	@Test
	public void testBoolAndExpression_valid() {
		assertNoProblems("true && true");
		assertNoProblems("true && false");
		assertNoProblems("false && {true}");
	}

	@Test
	public void testBoolAndExpression_bad() {
		assertHasProblems("true && 1");
		assertHasProblems("true && []");
		assertHasProblems("false && {5}");
	}

	@Test
	public void testBoolAndExpression_valid_variable() {
		assertNoProblems("_var && _var");
		assertNoProblems("_var && {_var}");
		assertNoProblems("true && _var");
		assertNoProblems("true && {_var}");
	}

	//----END Bool And Expression----

	//----START Bool Or Expression----
	@Test
	public void testBoolOrExpression_valid() {
		assertNoProblems("true || true");
		assertNoProblems("true || false");
		assertNoProblems("false || {true}");
	}

	@Test
	public void testBoolOrExpression_bad() {
		assertHasProblems("true || 1");
		assertHasProblems("true || []");
		assertHasProblems("false || {5}");
	}

	@Test
	public void testBoolOrExpression_valid_variable() {
		assertNoProblems("_var || _var");
		assertNoProblems("_var || {_var}");
		assertNoProblems("true || _var");
		assertNoProblems("true || {_var}");
	}
	//----END Bool Or Expression----

	//----START Bool Not Expression----
	@Test
	public void testBoolNotExpression_valid() {
		assertNoProblems("!true");
		assertNoProblems("!(true || false)");
		assertNoProblems("!(false || {true})");

		assertNoProblems("!_var");
	}

	@Test
	public void testBoolNotExpression_bad() {
		assertHasProblems("!1");
		assertHasProblems("![]");
		assertHasProblems("!{5}");
		assertHasProblems("!{true}");
	}
	//----END Bool Not Expression----

	//----START Comp Expression----
	@Test
	public void testCompExpression_valid() {
		assertNoProblems("1 < 1");
		assertNoProblems("1 < 0.5");

		assertNoProblems("1 <= 10");
		assertNoProblems("1 <= 0.5");

		assertNoProblems("1 > 1");
		assertNoProblems("1 > 0.5");

		assertNoProblems("1 >= 10");
		assertNoProblems("1 >= 0.5");

		assertNoProblems("1==1"); //number
		assertNoProblems("''==''"); //string

		{ //group
			assertEquals(ValueType.BaseType.GROUP, getExitTypeForText("grpNull"));
			assertNoProblems("grpNull==grpNull");
			assertNoProblems("grpNull!=grpNull");
		}

		{ //side
			assertEquals(BaseType.SIDE, getExitTypeForText("west"));
			assertNoProblems("west==west");
			assertNoProblems("west!=west");
		}

		{ //object
			assertEquals(BaseType.OBJECT, getExitTypeForText("objNull"));
			assertNoProblems("objNull==objNull");
			assertNoProblems("objNull!=objNull");
		}

		{ //config
			assertEquals(BaseType.CONFIG, getExitTypeForText("configFile"));
			assertNoProblems("configFile==configFile");
			assertNoProblems("configFile!=configFile");
		}

		{ //display
			assertEquals(ValueType.BaseType.DISPLAY, getExitTypeForText("displayNull"));
			assertNoProblems("displayNull==displayNull");
			assertNoProblems("displayNull!=displayNull");
		}

		{ //control
			assertEquals(ValueType.BaseType.CONTROL, getExitTypeForText("controlNull"));
			assertNoProblems("controlNull==controlNull");
			assertNoProblems("controlNull!=controlNull");
		}

		{ //location
			assertEquals(BaseType.LOCATION, getExitTypeForText("locationNull"));
			assertNoProblems("locationNull==locationNull");
			assertNoProblems("locationNull!=locationNull");
		}

		{ //structured text
			assertEquals(BaseType.STRUCTURED_TEXT, getExitTypeForText("parseText ''"));
			assertNoProblems("(parseText '')==(parseText '')");
			assertNoProblems("(parseText '')!=(parseText '')");
		}

	}

	@Test
	public void testCompExpression_bad() {
		assertHasProblems("1 < ''");
		assertHasProblems("1 < []");
		assertHasProblems("'' < []");
		assertHasProblems("[] < 1");
		assertHasProblems("[] < []");

		assertHasProblems("1 <= ''");
		assertHasProblems("1 <= []");
		assertHasProblems("'' <= []");
		assertHasProblems("[] <= 1");
		assertHasProblems("[] <= []");

		assertHasProblems("1 > ''");
		assertHasProblems("1 > []");
		assertHasProblems("'' > []");
		assertHasProblems("[] > 1");
		assertHasProblems("[] > []");

		assertHasProblems("1 >= ''");
		assertHasProblems("1 >= []");
		assertHasProblems("'' >= []");
		assertHasProblems("[] >= 1");
		assertHasProblems("[] >= []");

		assertHasProblems("1 == ''");
		assertHasProblems("[] == []");
		assertHasProblems("1 == []");
		assertHasProblems("'' == []");
		assertHasProblems("[] == 1");
		assertHasProblems("[] == false");
		assertHasProblems("grpNull==west");
		assertHasProblems("west==grpNull");
		assertHasProblems("objNull==1");
		assertHasProblems("configFile==[]");
		assertHasProblems("displayNull==''");
		assertHasProblems("controlNull==[]");
		assertHasProblems("locationNull==false");
		assertHasProblems("(parseText '')==0");

		//in SQF, you can't compare booleans with == and !=
		assertHasProblems("false == true");
		assertHasProblems("_var == true");
		assertHasProblems("true == _var");
		assertHasProblems("_var != true");
		assertHasProblems("true != _var");

		assertHasProblems("[] != []");
		assertHasProblems("1 != []");
		assertHasProblems("'' != []");
		assertHasProblems("[] != 1");
		assertHasProblems("[] != false");
		assertHasProblems("configFile!=[]");
		assertHasProblems("controlNull!=[]");
		assertHasProblems("1 != ''");
		assertHasProblems("grpNull!=west");
		assertHasProblems("west!=grpNull");
		assertHasProblems("objNull!=1");
		assertHasProblems("displayNull!=''");
		assertHasProblems("locationNull!=false");
		assertHasProblems("(parseText '')!=0");

	}

	@Test
	public void testCompExpression_valid_variable() {
		assertNoProblems("_var < _var");
		assertNoProblems("1 < _var");
		assertNoProblems("_var < 1");

		assertNoProblems("_var <= _var");
		assertNoProblems("1 <= _var");
		assertNoProblems("_var <= 1");

		assertNoProblems("_var > _var");
		assertNoProblems("1 > _var");
		assertNoProblems("_var > 1");

		assertNoProblems("_var >= _var");
		assertNoProblems("1 >= _var");
		assertNoProblems("_var >= 1");

		assertNoProblems("_var == _var");
		assertNoProblems("1 == _var");
		assertNoProblems("_var == 1");
		assertNoProblems("_var == configFile");
		assertNoProblems("configFile == _var");

		assertNoProblems("_var != _var");
		assertNoProblems("1 != _var");
		assertNoProblems("_var != 1");
		assertNoProblems("_var != configFile");
		assertNoProblems("configFile != _var");
	}
	//----END Comp Expression----

	//----START config fetch Expression----
	@Test
	public void testConfigFetchExpression_valid() {
		assertNoProblems("configFile >> ''");
		assertNoProblems("configFile >> '' >> ''");
		assertNoProblems("configFile >> '' >> '' >> ''");

		assertNoProblems("configFile >> _var >> '' >> _var");
		assertNoProblems("configFile >> _var");
		assertNoProblems("configFile >> '' >> _var >> _var");
		assertNoProblems("_var >> ''");
		assertNoProblems("_var >> _var");
	}

	@Test
	public void testConfigFetchExpression_bad() {
		assertHasProblems("configFile >> 1");
		assertHasProblems("configFile >> 1 >> 2");
		assertHasProblems("1 >> '' >> ''");
		assertHasProblems("_var >> 1 >> ''");
	}
	//----END config fetch Expression----

	//----START exponent Expression----
	@Test
	public void testExponentExpression_valid() {
		assertNoProblems("1^1");
		assertNoProblems("1^1^5");
		assertNoProblems("1^1^5^7");

		assertNoProblems("_var^1^5^7");
		assertNoProblems("_var^_var^5^7");
		assertNoProblems("_var^1^5^_var");
		assertNoProblems("1^1^_var^7");
		assertNoProblems("_var^_var^_var^_var");
	}

	@Test
	public void testExponentExpression_bad() {
		assertHasProblems("1^1^''");
		assertHasProblems("''^1^5");
		assertHasProblems("''^1^5^''");

		assertHasProblems("_var^''^5^7");
		assertHasProblems("''^_var^5^7");
		assertHasProblems("_var^1^''^_var");
		assertHasProblems("''^''");
	}
	//----END exponent Expression----

	//----START signed Expression----
	@Test
	public void testSignExpression_valid() {
		assertNoProblems("1+ +1");
		assertNoProblems("[]+ +[]");
		assertNoProblems("+1");
		assertNoProblems("+1.5");
		assertNoProblems("+1e1");
		assertNoProblems("+[]");
		assertNoProblems("+_var");

		assertNoProblems("-1");
		assertNoProblems("-1.5");
		assertNoProblems("-1e1");
		assertNoProblems("-_var");
	}

	@Test
	public void testSignExpression_bad() {
		assertHasProblems("+configFile");
		assertHasProblems("+''");

		assertHasProblems("-[]");
		assertHasProblems("-''");
	}
	//----END signed Expression----

	//----START code block Expression----
	@Test
	public void testCodeBlockExpression() {
		assertNoProblems("{1}");
		assertNoProblems("{+1.5}");
		assertNoProblems("{}");

		assertEquals(ValueType.BaseType.CODE, getExitTypeForText("{}"));
		assertEquals(BaseType.CODE, getExitTypeForText("{1}"));
		assertEquals(ValueType.BaseType.CODE, getExitTypeForText("{_var}"));
		assertEquals(BaseType.CODE, getExitTypeForText("{1+1;1}"));
	}
	//----END code block Expression----

	//----START case statement----
	@Test
	public void testCaseStatement() {
		assertNoProblems("case 1;");
		assertNoProblems("case 2:{};");
		assertNoProblems("case '';");
		assertNoProblems("case [];");
		assertNoProblems("case []:{};");
		assertNoProblems("case []:{1};");

		assertNoProblems("case configFile;");

		assertNoProblems("case _var:{};");
		assertNoProblems("case _var;");

		assertEquals(BaseType.NOTHING, getExitTypeForText("case 1:{};"));
		assertEquals(BaseType.NOTHING, getExitTypeForText("case 1:{2};"));
		assertEquals(BaseType.NOTHING, getExitTypeForText("case 1;"));
		assertEquals(ValueType.BaseType.NOTHING, getExitTypeForText("case configFile;"));
	}
	//----END case statement----

	//----START assignment statement----
	@Test
	public void testAssignmentStatement() {
		assertNoProblems("a = {};");
		assertNoProblems("a={2};");
		assertNoProblems("a = 1+1;");

		assertNoProblems("_var = configFile;");
		assertNoProblems("_var = _var;");

		//this problem should be a grammar error, not a type error
		assertNoProblems("a = ;");

		assertEquals(ValueType.BaseType.NOTHING, getExitTypeForText("a = {};"));
		assertEquals(ValueType.BaseType.NOTHING, getExitTypeForText("a={2};"));
		assertEquals(ValueType.BaseType.NOTHING, getExitTypeForText("a = 1+1;"));
		assertEquals(ValueType.BaseType.NOTHING, getExitTypeForText("a = _var;"));
	}
	//----END assignment statement----

	//----START quest statement----
	@Test
	public void testQuestStatement_valid() {
		assertNoProblems("? true : false;");
		assertNoProblems("? true : {};");
		assertNoProblems("? _var : 1+1;");

		//this problem should be a grammar error, not a type error
		assertNoProblems("? ;");

		assertEquals(BaseType.NOTHING, getExitTypeForText("? true : false;"));
		assertEquals(BaseType.NOTHING, getExitTypeForText("? ;"));
	}

	@Test
	public void testQuestStatement_bad() {
		assertHasProblems("? 1 : false;");
		assertHasProblems("? {} : false;");
	}
	//----END quest statement----

	//----START command expression----
	@Test
	public void testCommandExpression_randomExpressions_valid() {
		assertNoProblems("player setUnitTrait ['Medic', true];");
		assertNoProblems("for '_n' from 0 to (round ((((_Power/5) max 10)+((round (random (_Power*5))) min 20))*((diag_fps/60) min 1) / ((viewDistance/(viewDistance-((positionCameraToWorld [0,0,0]) distance _Position))) max 1))) step 1 do {};");
		assertNoProblems("while {isNull assignedTarget _unit and (behaviour (effectiveCommander _unit) in ['COMBAT', 'AWARE', 'STEALTH']) and _ec == effectiveCommander _unit} do{};");
		assertNoProblems("if (abs _diff > _th and currentCommand _tank != 'MOVE' and speed _tank < 1) then {};");
		assertNoProblems("if (_x and _unit_wep select _j) then {};");
		assertNoProblems("if (_dir_y == 0 and _dir_x == 0) then{};");
		assertNoProblems("if (_ap_round != '' and _has_cannon) then{};");
		assertNoProblems("if (time - _last_hvinf > 2) then{};");
		assertNoProblems("if(_default_mag == _he_round and _has_he and _dist_aimed > 300) then {};");
		assertNoProblems("if((!(isNull commander _arty) && gunner _arty == _caller && isPlayer (commander _arty))) then {};");
		assertNoProblems("if ((_vehicle getVariable ['LIB_ARTY_AI_DIR', true]) && {!isPlayer (gunner _vehicle)} && {!isPlayer(commander _vehicle)}) then{};");
		assertNoProblems("for '_i' from 0 to _getCargo - 1 do {};");
		assertNoProblems("while {(getDammage _gun) < 0.7 && (_gun getVariable 'LIB_ARTY_MOVING_TOWING')} do{};");
		assertNoProblems("if(!(_x in _LIB_vehicles) && {!(_x getVariable ['LIB_CHECK_VEHICLE', false])} && {(_x isKindOf 'LIB_tank_base')}) then{};");
		assertNoProblems("setViewDistance (viewDistance + (_logic getVariable ['DISTANCE', (viewDistance * 2)]));");
		assertNoProblems("vehicle player isKindOf 'Plane'");
		assertNoProblems("if(_numArr select _i < 0 && count _numArr > 1) then{};");
		assertNoProblems("_ctrl ctrlSetStructuredText parseText localize \"str_vsm_no_vehicle\";");
		assertNoProblems("_ctrl ctrlSetStructuredText parseText '';");
		assertNoProblems("parseText localize \"str_vsm_no_vehicle\";");
		assertNoProblems("if(count _numArr == 0) exitWith{};");
		assertNoProblems("if((_i + 1) >= count _numArr || (_i + 1) >= count _opNumArr) exitWith{};");
		assertNoProblems("if(_numArr select _i > _opNumArr select _i) then {};");
		assertNoProblems("_numArr select _i == _opNumArr select _i");
		assertNoProblems("[_mhq, [_deployLabel, { [_this select 0] call MHQ_fnc_mhqDeployAction; }, [], 6, false, false, \"\", \"(speed (vehicle _target)) < 1 && (getPosATL _target) select 2 < 2\"]] remoteExec [\"addAction\", 0, true]");
		assertNoProblems("for \"_i\" from 0 to 1 do {};");
		assertNoProblems("for \"_i\" from 0 to 1 step -1 do {};");
		assertNoProblems("if(_numArr select _i == 0 && _i != 0) then{};");
		assertNoProblems("for [{_i = 0}, {_i < count _numArr - 1}, {_i = _i + 1}] do{};");
	}

	@Test
	public void testCommandExpression_githubReports_valid() {
		//all these tests are from github issues page: https://github.com/kayler-renslow/arma-intellij-plugin/issues/

//		assertNoProblems("AAS_JIPplayer = not isServer && isNull player;");//55
		assertNoProblems("_ammoType = getText(configFile/'CfgMagazines'/_x/'ammo')"); //56
		assertNoProblems("_target setPosASL [(eyePos _unit select 0) + 10 * sin _angle, (eyePos _unit select 1) + 10 * cos _angle, eyePos _unit select 2];"); //57
		assertNoProblems("if((missionNameSpace getVariable ['LIB_Explosion_Effect_Intensity', 0]) <= 0) then {};");//58
		assertNoProblems("_gun animate ['Rotation_Chassis', 1];");//59
		assertNoProblems("toLower(player getVariable ['LIB_Static_Weapon_Magazine', ''])"); //60
		assertNoProblems("diag_log format['***Magazine found in player's backpack']");//61
		assertNoProblems("player setPos [0,0]"); //62
	}

	@Test
	public void testCommandExpression_randomExpressions_bad() {
		assertHasProblems("createVehicle");
	}

	@Test
	public void testCommandExpression_ifThen_valid() {
		try {
			SQFCommands.instance.fromId(0);
		} catch (UnknownIdException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("todo");
//		assertExitTypeAndNoProblems("if true then {};", null, BaseType.ANYTHING);
//		assertExitTypeAndNoProblems("if true then {} else {};", null, BaseType.ARRAY);
//		assertExitTypeAndNoProblems("if true then [{},{}];", null, BaseType.ANYTHING);
//		assertExitTypeAndNoProblems("if true then [{},{},{}];", null, BaseType.ANYTHING);
	}

	@Test
	public void testCommandExpression_ifThen_bad() {
		assertHasProblems("if 1 then {};");
		assertHasProblems("if true then {} else;");
		assertHasProblems("if true then [{}];");
		assertHasProblems("if true then [];");
		assertHasProblems("if true then [{},1];");
		assertHasProblems("if true then [1,1];");
		assertHasProblems("if then {};");
		assertHasProblems("if true then;");
	}

	@Test
	public void testCommandExpression_needSemicolon() {
		assertHasProblems("true true;"); //need semicolon
		assertHasProblems("true 1;"); //need semicolon
	}

	@Test
	public void testCommandExpression_optionalParameters() {
		//fake a command syntax to assert that the syntax xml isn't the one creating false positives/negatives
		SQFCommand d1 = new SQFCommand("getPos", Arrays.asList(
				new SQFCommandSyntax(
						null,
						new ArrayParam(
								false,
								Arrays.asList(
										new Param("required", BaseType.NUMBER, "", false),
										new Param("optional", ValueType.BaseType.NUMBER, "", true)
								),
								true
						),
						new ReturnValueHolder(BaseType.VOID, "")
				)
		), "", BIGame.UNKNOWN);

		SQFCommand dd = new SQFCommand("position", Arrays.asList(
				new SQFCommandSyntax(
						new Param("optionalPrefix", BaseType.NUMBER, "", true),
						new ArrayParam(
								false,
								Arrays.asList(
										new ArrayParam(
												false,
												Arrays.asList(
														new Param("required", ValueType.BaseType.CODE, "", false)
												),
												true
										),
										new Param("optional", ValueType.BaseType.NUMBER, "", true)
								),
								true
						),
						new ReturnValueHolder(BaseType.CONFIG, "")
				)
		), "", BIGame.UNKNOWN);


//		assertExitTypeAndNoProblems("getPos [1,1];", cluster, ValueType.BaseType.VOID);
//		assertExitTypeAndNoProblems("getPos [1];", cluster, ValueType.BaseType.VOID);
//		assertExitTypeAndNoProblems("getPos;", cluster, ValueType.BaseType.VOID);
//		assertHasProblems("1 getPos"); //can't have prefix
//
//		assertExitTypeAndNoProblems("position [[{}],1];", cluster, ValueType.BaseType.CONFIG);
//		assertExitTypeAndNoProblems("position [[{}]];", cluster, BaseType.CONFIG);
//		assertExitTypeAndNoProblems("position [];", cluster, ValueType.BaseType.CONFIG);
//
//		assertExitTypeAndNoProblems("0 position;", cluster, BaseType.CONFIG);
//		assertExitTypeAndNoProblems("0 position [[{}],1];", cluster, ValueType.BaseType.CONFIG);
//		assertExitTypeAndNoProblems("0 position [[{}]];", cluster, BaseType.CONFIG);
//		assertExitTypeAndNoProblems("0 position [];", cluster, BaseType.CONFIG);
//		assertExitTypeAndNoProblems("0 position;", cluster, BaseType.CONFIG);
//
//		assertExitTypeAndNoProblems("position;", cluster, ValueType.BaseType.CONFIG);
		throw new RuntimeException("todo");
	}
	//----END command expression----
}
