#!/bin/bash

file=vs4e_0.9.12.I20090527-2200.zip
url="https://visualswing4eclipse.googlecode.com/files/$file"

if [ ! -f "$file" ]; then
  echo "[Download archive]"
  curl -o "$file" "$url"
  echo "[Done]"
fi

unzip -u "$file"

gid=org.dyno.visual.swing
for f in plugins/*.jar; do
  base=$(basename "$f" .jar)
  name=${base%_*}
  aid=${name#*.*.}
  ver=${base#*_}
  mvn install:install-file \
    -Dfile="$f" \
    -DgroupId="$gid" \
    -DartifactId="$aid" \
    -Dversion="$ver" \
    -Dpackaging="jar" \
    -DgeneratePom="true"
done
