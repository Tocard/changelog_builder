#!/usr/bin/env bash
set +x
set -e

function version_memory {
    MAJOR=`expr ${MAJOR} + ${MAJOR_GREP};`
    MINOR=`expr ${MINOR} + ${MINOR_GREP};`
    BUG=`expr ${BUG} + ${BUG_GREP};`
}

function calculator {
if [ ${MAJOR} -gt 0 ]
    then
        MAJOR_GIT=`echo "$((${MAJOR_GIT}+1))";`
        MINOR_GIT=`echo 0`
        BUG_GIT=`echo 0`
    elif [ ${MINOR} -gt 0 ]
    then
        MINOR_GIT=`echo "$((${MINOR_GIT}+1))";`
        BUG_GIT=`echo 0`
    elif [ ${BUG} -gt 0 ]
    then
       BUG_GIT=`echo "$((${BUG_GIT}+1))";`
fi
echo "${MAJOR_GIT}.${MINOR_GIT}.${BUG_GIT}";
}

function get_tag {
BUG=0
MINOR=0
MAJOR=0
SIZE=`grep -n "Init de la nouvelle branche de dev" changelog.adoc | cut -f1 -d: | head -1`
head -n+${SIZE} changelog.adoc > change.txt;
while read -r LINE; do
    BUG_GREP=`echo $LINE  | grep -c 'Bugfix\|Hotfix\|Task\|Undefined\|bugfix\|hotfix\|task\|undefined\|Bug\|bug'`
    MINOR_GREP=`echo $LINE  | grep -c 'Feature\|feature\|Evol\|evol'`
    MAJOR_GREP=`echo $LINE  | grep -c 'Refonte\|refonte'`
    version_memory
done < change.txt
rm change.txt;
echo $(calculator)
}

cd ${WORKSPACE}/project
VERSION=`git describe --tags --abbrev=0 $(git rev-list --tags --max-count=1) --match '*.*.*[0-9]'`
MAJOR_GIT=`echo ${VERSION} | cut -d "." -f1`
MINOR_GIT=`echo ${VERSION} | cut -d "." -f2`
BUG_GIT=`echo ${VERSION} | cut -d "." -f3`


NEW_TAG=$(get_tag)
echo -n "export TAG_VERSION=" > ${WORKSPACE}/project/tag.variables
echo ${NEW_TAG} | cut -d '-' -f 2 >> ${WORKSPACE}/project/tag.variables
tail -n+7  changelog.adoc > tmp;
sed -i "s/Version in progress/link:{uri-version}${NEW_TAG}[Version ${NEW_TAG}]/g" tmp;

echo ":uri-pr: https://repo_bitbucket/projects/${GIT_PROJECT_NAME}/repos/${GIT_REPOSITORY_NAME}/pull-requests" > changelog.adoc;
echo ":uri-jira: https://jira/browse/" >> changelog.adoc;
echo ":uri-icon: https://repo_bitbucket/users/youruser/repos/logo_jira/raw">> changelog.adoc; #optional, this was to host cusmtom images
echo ":uri-version: https://repo_bitbucket/projects/${GIT_PROJECT_NAME}/repos/${GIT_REPOSITORY_NAME}/browse?at=refs%2Ftags%2F" >> changelog.adoc;
echo ":OPTION: options='header', width=\"100%, cols='^m,^m,^m,^m,^'\"" >> changelog.adoc;
echo -n '''= Changelog

== Version en cours de Developpement
[{OPTION}]
|===
a|Date a| Comment a| Type a| Ticket a| PR

a| '`date +%d/%m/%y`' a| Init of the new dev cycle a| image:{uri-icon}/conf.svg[] a| Not a jira  a| Not a PR
|===

''' >> changelog.adoc;

cat tmp >> changelog.adoc;
rm tmp;
git tag ${NEW_TAG}
git checkout master
git push origin ${NEW_TAG}
git add changelog.adoc;
git commit -m "New dev cycle";
git push origin;
