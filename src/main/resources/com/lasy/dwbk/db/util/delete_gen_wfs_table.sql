-- delete generated table
DROP TABLE ${tableName};

-- delete entry from gpkg_contents
DELETE FROM gpkg_contents WHERE table_name = '${tableName}';

-- delete entry from gpkg_geometry_columns
DELETE FROM gpkg_geometry_columns WHERE table_name = '${tableName}';