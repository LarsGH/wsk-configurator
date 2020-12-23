#### Einrichtung

Umgebungsvariable "DWBK_CONFIG_DIRECTORY": muss auf Ordner zeigen, in welchem die Konfiguration erzeugt wird.
	In dem definierten Ordner wird die Datenbank (dwbk_db.gpkg) abgelegt. 
	Weiterhin wird hier auch das Logfile erzeugt.
	
Umgebungsvariable "DWBK_CONFIG_DEBUG_LEVEL": legt das Debug-Level und somit auch das Logging-Level fest.

## Start GUI test
mvn clean javafx:run

# debug mode!
mvn clean javafx:run@debug
mvn clean javafx:run@run


# load install jar
java -jar dwbk-configurator.jar