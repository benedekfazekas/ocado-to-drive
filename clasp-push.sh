#!/bin/bash

lein do clean, cljsbuild once main
echo "hacking the export"
cat src/entry_points.js > export/OcadoToDrive_tmp.gs
echo -e "\n" >> export/OcadoToDrive_tmp.gs
cat export/OcadoToDrive.gs >> export/OcadoToDrive_tmp.gs
echo "cleaning up"
mv export/OcadoToDrive_tmp.gs export/OcadoToDrive.gs

echo "pushing the following"
clasp status
echo "pushing..."
clasp push
