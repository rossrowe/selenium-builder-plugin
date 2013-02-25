This project contains the source code for the Jenkins/Hudson [Selenium Builder](http://sebuilder.github.com/se-builder/) plugin,
which will run a WebDriver instance over a Selenium Builder JSON file.

To build and unit test the plugin, execute:
	
	mvn package
	
To deploy a built version, execute

	mvn release:prepare release:perform
