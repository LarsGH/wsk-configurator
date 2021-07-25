#### Einrichtung

Umgebungsvariable "WSK_CONFIG_DIRECTORY": muss auf Ordner zeigen, in welchem die Konfiguration erzeugt wird.
	In dem definierten Ordner wird die Datenbank (wsk_db.gpkg) abgelegt.
	Weiterhin werden hier auch das Logfile (wsk.log) und die Settings (wsk_settings.json) erzeugt.


#### Setup (ENG)

environment variable "WSK_CONFIG_DIRECTORY": needs to point to a directory where the configuration will be created.
    The database (wsk_db.gpkg) will be created in the defined directory.
    Moreover the logfile (wsk.log) and the settings (wsk_settings.json) will be created in this directory


# Start GUI
mvn clean javafx:run

# debug mode!
mvn clean javafx:run@debug
mvn clean javafx:run@run

# load install jar
java -jar wsk-configurator.jar