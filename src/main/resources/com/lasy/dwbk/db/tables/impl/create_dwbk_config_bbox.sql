-- create bbox config table
CREATE TABLE "dwbk_config_bbox" (
"id" INTEGER PRIMARY KEY AUTOINCREMENT, 
"name" VARCHAR(40) NOT NULL UNIQUE, 
"description" VARCHAR(255),
"epsg" MEDIUMINT NOT NULL, 
"is_map_boundary" VARCHAR(1) NOT NULL, 
"min_lon" VARCHAR(40) NOT NULL, 
"min_lat" VARCHAR(40) NOT NULL, 
"max_lon" VARCHAR(40) NOT NULL, 
"max_lat" VARCHAR(40) NOT NULL,
"last_changed" VARCHAR(19) NOT NULL);

-- add to available tables
INSERT INTO gpkg_contents
(table_name, data_type, identifier, description, last_change)
VALUES('dwbk_config_bbox', 'features', 'dwbk_config_bbox', 'dwbk_config_bbox', CURRENT_TIMESTAMP);