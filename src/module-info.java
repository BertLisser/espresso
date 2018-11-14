/**
 * 
 */
/**
 * @author bertl
 *
 */
module espresso {
	requires transitive javafx.controls;
	requires transitive jdk.jsobject;
	//requires jdk.jsobject;
	requires transitive javafx.web;
	requires transitive javafx.graphics;
	requires transitive javafx.base;
	exports espresso;
}