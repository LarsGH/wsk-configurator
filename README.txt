#### Einrichtung

Umgebungsvariable "WSK_CONFIG_DIRECTORY": muss auf Ordner zeigen, in welchem die Konfiguration erzeugt wird.
	In dem definierten Ordner wird die Datenbank (wsk_db.gpkg) abgelegt.
	Weiterhin werden hier auch das Logfile (wsk.log) und die Settings (wsk_settings.json) erzeugt.

## Start GUI test
mvn clean javafx:run

# debug mode!
mvn clean javafx:run@debug
mvn clean javafx:run@run


# load install jar
java -jar wsk-configurator.jar