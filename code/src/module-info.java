module Orinoco.SQF {
	requires annotations;
	requires transitive javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive javafx.graphics;
	requires transitive javafx.media;
	requires transitive javafx.swing;
	requires transitive javafx.web;
	requires javafx.swt;
	requires junit;

	exports arma.orinocosqf;
	exports arma.orinocosqf.configuration;
	exports arma.orinocosqf.exceptions;
	exports arma.orinocosqf.gui;
	exports arma.orinocosqf.lexer;
	exports arma.orinocosqf.parsing.postfix;
	exports arma.orinocosqf.preprocessing;
	exports arma.orinocosqf.preprocessing.bodySegments;
	exports arma.orinocosqf.problems;
	exports arma.orinocosqf.queue;
	exports arma.orinocosqf.sqf;
	exports arma.orinocosqf.tokenprocessing;
	exports arma.orinocosqf.type;
	exports arma.orinocosqf.util;
}