This project contains the source code for the Jenkins/Hudson [Selenium Builder](http://sebuilder.github.com/se-builder/) plugin.

The plugin enables Jenkins Jobs to run a Selenium Builder JSON file using the [Selenium Builder Interpreter](https://github.com/sebuilder/se-builder/tree/master/tools/seinterpreter).

To build and unit test the plugin, execute:
	
	mvn package
	
To deploy a built version, execute

	mvn release:prepare release:perform
