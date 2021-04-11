-- delete generated table
DROP TABLE ${tableName};

-- delete entry from gpkg_contents
DELETE FROM gpkg_contents WHERE table_name = '${tableName}';

-- delete entry from gpkg_tile_matrix
DELETE FROM gpkg_tile_matrix WHERE table_name = '${tableName}';

-- delete entry from gpkg_tile_matrix_set
DELETE FROM gpkg_tile_matrix_set WHERE table_name = '${tableName}';