-- create layer config table
CREATE TABLE "wsk_config_layer" (
"id" INTEGER PRIMARY KEY AUTOINCREMENT, 
"name" VARCHAR(40) NOT NULL UNIQUE, 
"description" VARCHAR(255),
"store_local" VARCHAR(1) NOT NULL,
"is_visible" VARCHAR(1) NOT NULL, 
"request" VARCHAR(1000) NOT NULL, 
"bbox_id" MEDIUMINT NOT NULL,
"local_name" VARCHAR(40),
"last_dl" VARCHAR(19),
"last_changed" VARCHAR(19) NOT NULL,
"service" VARCHAR(3) NOT NULL,
"user" VARCHAR(40), 
"pw" VARCHAR(40),
"service_config" VARCHAR(2000) NOT NULL,
FOREIGN KEY("bbox_id") REFERENCES wsk_config_bbox("id")
);

-- add to available tables
INSERT INTO gpkg_contents
(table_name, data_type, identifier, description, last_change)
VALUES('wsk_config_layer', 'features', 'wsk_config_layer', 'wsk_config_layer', CURRENT_TIMESTAMP);