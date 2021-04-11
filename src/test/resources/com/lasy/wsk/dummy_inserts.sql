-- BBOX
INSERT INTO wsk_config_bbox (id, name, description, epsg, is_map_boundary, min_lon, min_lat, max_lon, max_lat, last_changed) 
VALUES
(1, 'Dorsten Holsterhausen', 'Ortsteil Dorsten Holsterhausen', 4326, 'N', '6.926850', '51.669430', '6.971011', '51.687067', '2020-12-28T09:39:44'),
(2, 'Dorsten Gesamt', 'Gesamtes Stadtgebiet Dorsten', 4326, 'Y', '6.887640', '51.636216', '7.083052', '51.781193', '2020-12-28T09:39:44'),
(3, 'Radweg', 'Radweg mit Rettungspunkten', 4326, 'N', '6.922229', '51.673266', '6.947550', '51.677131', '2021-01-16T14:04:22');


-- LAYER
INSERT INTO wsk_config_layer(id, name, description, store_local, is_visible, request, bbox_id, "user", pw, local_name, last_dl, last_changed, service, service_config)
VALUES(1, 'Luftbild NRW', 'Luftbild NRW in Farbe', 'Y', 'Y', 'https://www.wms.nrw.de/geobasis/wms_nw_dop?service=WMS&version=1.0.0&request=GetCapabilities', 2, NULL, NULL, 'wsk_genlayer_on', NULL, '2021-02-08T17:27:44', 'WMS', '{
  "layer": "nw_dop_rgb",
  "styles": "default",
  "format": "image/png",
  "isTransparent": true,
  "metersPerPixel": [
    2
  ],
  "requestEpsg": 3857
}'),
(2, 'Flurstücke NRW', 'Flurstücke in NRW', 'N', 'N', 'https://www.wms.nrw.de/geobasis/wms_nw_inspire-flurstuecke_alkis?service=WMS&version=1.3.0&request=GetCapabilities', 1, NULL, NULL, 'wsk_genlayer_tw', NULL, '2021-02-08T17:29:57', 'WMS', '{
  "layer": "CP.CadastralParcel",
  "styles": "CP.CadastralParcel.Default",
  "format": "image/png",
  "isTransparent": true,
  "requestEpsg": 3857
}'),
(3, 'Gemeindegrenzen', 'Gemeindegrenzen in NRW', 'N', 'N', 'https://www.wms.nrw.de/geobasis/wms_nw_dvg?service=WMS&version=1.3.0&request=GetCapabilities', 2, NULL, NULL, 'wsk_genlayer_th', NULL, '2021-02-08T17:34:38', 'WMS', '{
  "layer": "nw_dvg_gem",
  "styles": "default",
  "format": "image/png",
  "isTransparent": true,
  "requestEpsg": 3857
}'),
(4, 'Notfall-Informations-Punkte', 'NIPs im Kreis RE', 'N', 'N', 'https://geoservice.gkd-re.de/wss/service/KreisRE_KRE-I03_BEVSCHUTZ/guest?service=WMS&version=1.3.0&request=GetCapabilities', 2, NULL, NULL, 'wsk_genlayer_fo', NULL, '2021-02-08T17:36:30', 'WMS', '{
  "layer": "NOTFALLINFOPUNKTE",
  "styles": "Infopunkte",
  "format": "image/png",
  "isTransparent": true,
  "requestEpsg": 25832
}'),
(5, 'Rettungspunkte', 'Rettungspunkte im Kreis RE', 'N', 'N', 'https://geoservice.gkd-re.de/wss/service/KreisRE_KRE-I03_BEVSCHUTZ/guest?service=WMS&version=1.3.0&request=GetCapabilities', 2, NULL, NULL, 'wsk_genlayer_fi', NULL, '2021-02-08T17:38', 'WMS', '{
  "layer": "RETTUNGSPUNKTE",
  "styles": "BEVSchutz:Rettungspunkte",
  "format": "image/png",
  "isTransparent": true,
  "requestEpsg": 25832
}'),
(6, 'Stadtteile Dorsten', 'Dorstener Stadtteile', 'N', 'N', 'https://geoservice.gkd-re.de/wss/service/KreisRE_KRE-I03_GEBIETE/guest?service=WMS&version=1.3.0&request=GetCapabilities', 2, NULL, NULL, 'wsk_genlayer_si', NULL, '2021-02-08T17:39:29', 'WMS', '{
  "layer": "STADTTEILE_DORSTEN",
  "styles": "Stadtteile",
  "format": "image/png",
  "isTransparent": true,
  "requestEpsg": 25832
}'),
(7, 'Notfalll-Info-Punkte', 'NIPs im Kreis RE', 'N', 'N', 'https://geoservice.gkd-re.de/wss/service/KreisRE_WFS_KRE-I03_BEVSCHUTZ/guest?service=WFS&version=1.0.0&request=GetCapabilities', 2, NULL, NULL, 'wsk_genlayer_se', NULL, '2021-02-08T17:41:06', 'WFS', '{
  "typeNames": "BEVSchutz:NOTFALLINFOPUNKTE",
  "requestEpsg": 25832,
  "styleConfig": {
    "geomType": "POINT",
    "lineColor": "255;0;0;1",
    "fillColor": "0;100;0;1"
  }
}'),
(8, 'Rettungspunkte WFS', 'Rettungspunkte im Kreis RE', 'N', 'N', 'https://geoservice.gkd-re.de/wss/service/KreisRE_WFS_KRE-I03_BEVSCHUTZ/guest?service=WFS&version=1.0.0&request=GetCapabilities', 2, NULL, NULL, 'wsk_genlayer_ei', NULL, '2021-02-08T17:43:13', 'WFS', '{
  "typeNames": "BEVSchutz:RETTUNGSPUNKTE",
  "requestEpsg": 25832,
  "styleConfig": {
    "geomType": "POINT",
    "lineColor": "255;0;0;1",
    "fillColor": "139;0;139;1"
  }
}'),
(9, 'Sperrungen WFS', 'Sperrungen im Kreis RE', 'N', 'N', 'https://geoservice.gkd-re.de/wss/service/KreisRE_WFS_KRE-I03_BEVSCHUTZ/guest?service=WFS&version=1.0.0&request=GetCapabilities', 2, NULL, NULL, 'wsk_genlayer_ni', NULL, '2021-02-08T17:44:45', 'WFS', '{
  "typeNames": "BEVSchutz:SPERRUNGEN",
  "requestEpsg": 25832,
  "styleConfig": {
    "geomType": "POLYGON",
    "lineColor": "255;0;0;1",
    "fillColor": "139;0;139;50"
  }
}'),
(10, 'Stadtteile Dorsten WFS', 'Dorstener Stadtteile', 'N', 'N', 'https://geoservice.gkd-re.de/wss/service/KreisRE_WFS_KRE-I03_GEBIETE/guest?service=WFS&version=1.0.0&request=GetCapabilities', 2, NULL, NULL, 'wsk_genlayer_onze', NULL, '2021-02-08T17:45:47', 'WFS', '{
  "typeNames": "Gebietsgliederung:STADTTEILE_DORSTEN",
  "requestEpsg": 25832,
  "styleConfig": {
    "geomType": "POLYGON",
    "lineColor": "255;0;0;1"
  }
}');
