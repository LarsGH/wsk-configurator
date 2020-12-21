-- create layer config table
CREATE TABLE "dwbk_config_layer" (
"id" INTEGER PRIMARY KEY AUTOINCREMENT, 
"name" VARCHAR(40) NOT NULL UNIQUE, 
"description" VARCHAR(255),
"is_saved" VARCHAR(1) NOT NULL, 
"store_local" VARCHAR(1) NOT NULL, 
"uri" VARCHAR(255) NOT NULL UNIQUE, 
"bbox_id" MEDIUMINT, 
"user" VARCHAR(40), 
"pw" VARCHAR(40),
"last_changed" VARCHAR(19) NOT NULL,
FOREIGN KEY("bbox_id") REFERENCES dwbk_config_bbox("id")
);

-- add to available tables
INSERT INTO gpkg_contents
(table_name, data_type, identifier, description, last_change)
VALUES('dwbk_config_layer', 'features', 'dwbk_config_layer', 'dwbk_config_layer', CURRENT_TIMESTAMP);