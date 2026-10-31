#!/bin/sh

set -eu

./gradlew \
    --no-scan \
    :internal:dokka:dokkaGenerate

rm -rf docs/api/
cp -r internal/dokka/build/dokka/html/ docs/api/

cp CHANGELOG.md docs/changelog.md

mkdir -p docs/javascripts
curl --fail --location --silent --show-error \
  --output docs/javascripts/snapshot-metadata.xml \
  https://central.sonatype.com/repository/maven-snapshots/dev/chrisbanes/haze/haze/maven-metadata.xml

properdocs "$@"
