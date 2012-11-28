The conceptstore project allows you to store and retrive concepts and their relationships from a graph database.
The project has 3 modules:
	1. conceptstore-graph-impl: actual implementation of the conceptstore
	2. conceptstore-dataimport: batch import script for importing data into the conceptstore
	3. conceptstore-client: client code with examples on how to access the conceptstore.

1. The projects are built using maven and can be directly imported into eclipse if you have the m2e plugin.
2. Eclipse project files can be built using the "mvn eclipse:eclipse" command
3. Full jar with and without dependencies in created in the "target" folder by running the "mvn package" command. "xxx-jar-with-dependencies" is the jar with all the dependencies included.
Be aware that inclusion of "jars-with-dependencies" can cause conflicts if you use a different version of the same packages in your project
