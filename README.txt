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

Es muss zwingend eine Boundingbox angelegt werden, welche die Karten-Begrenzung festlegt!

Dokumentation:

## Layer-Konfiguration
- Die URI muss einen GetMap-Request sein.
	- Die URI muss im Browser das gewünschte Raster darstellen. Konfigurationsfehler sollten hier bereits auffallen.
	- Für den kompletten Layer wird die konfigurierte Boundingbox verwendet (und nicht die bbox aus der URI). 
	- Es sollten alle Parameter angegeben werden, welche berücksichtigt werden sollen (insbesondere die Pflichtparameter...)
- WMS-Layer werden lokal immer in "EPSG:3857" gespeichert.
	- Wenn ein Layer nicht in "EPSG:3857" angefragt werden kann, wird das Koordinatensystem der GetMap-URI verwendet und anschließend transformiert
	- Zum Erstellen der Raster-Pyramide werden alle Konfigurierten Genauigkeiten berücksichtigt
		- Die Genauigkeit wird in ganzen Metern angegeben
		- Erlaubte Genauigkeiten sind ausschließlich Teiler von 500 (500 % <Genauigkeit> = 0) ?!
		- Mehrere Genauigkeiten werden durch Semikolon (';') getrennt angegeben