#!/usr/bin/env bash
set +x
set -e

WORKSPACE_PROJECT="${WORKSPACE}/project"
if [ ! -d ${WORKSPACE_PROJECT} ]; then
    mkdir -p ${WORKSPACE_PROJECT}
fi

cd ${WORKSPACE}/project

git checkout ${BRANCH}

sed -i "s/NPR/${ID_NPR}/g" changelog.adoc

git add changelog.adoc
git commit -m "Numéro de PR automatique"
git push origin ${BRANCH}


