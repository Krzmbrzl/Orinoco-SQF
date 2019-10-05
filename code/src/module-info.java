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

	exports arma.orinocosqf.gui;
	exports arma.orinocosqf.sqf;
}