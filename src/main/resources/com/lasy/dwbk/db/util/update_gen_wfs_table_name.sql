-- update generated table
ALTER TABLE '${tableName}' RENAME TO '${newTableName}';

-- update entry in gpkg_contents
UPDATE gpkg_contents SET 
  table_name = '${newTableName}',
  identifier = '${newTableName}',
  description = '${newTableName}'
WHERE table_name = '${tableName}';

-- update entry in gpkg_geometry_columns
UPDATE gpkg_geometry_columns SET table_name = '${newTableName}' WHERE table_name = '${tableName}';