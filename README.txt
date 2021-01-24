#### Einrichtung

Umgebungsvariable "DWBK_CONFIG_DIRECTORY": muss auf Ordner zeigen, in welchem die Konfiguration erzeugt wird.
	In dem definierten Ordner wird die Datenbank (dwbk_db.gpkg) abgelegt.
	Weiterhin werden hier auch das Logfile (dwbk.log) und die Settings (dwbk_settings.json) erzeugt.

## Start GUI test
mvn clean javafx:run

# debug mode!
mvn clean javafx:run@debug
mvn clean javafx:run@run


# load install jar
java -jar dwbk-configurator.jar