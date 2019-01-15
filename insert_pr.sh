#!/usr/bin/env bash
set +x
set -e

WORKSPACE_PROJECT="${WORKSPACE}/project"
if [ ! -d ${WORKSPACE_PROJECT} ]; then
    mkdir -p ${WORKSPACE_PROJECT}
fi

cd ${WORKSPACE}/project

git checkout ${BRANCH}

head -n+12  changelog.adoc > tmp;

# this pattern expected  is TYPE/TICKET/ACT

ACT=`git branch | grep \* | cut -d ' ' -f2 | cut -d "/" -f2 | cut -d "-" -f3;`
TICKET=`git branch | grep \* | cut -d ' ' -f2 | cut -d "/" -f2 | cut -d "-" -f2;`
TYPE=`git branch | grep \* | cut -d ' ' -f2 | cut -d "/" -f1;`

if [[ ${TICKET} =~ [^[:digit:]] ]]
then
    JIRA="No Jira"
else
    JIRA="{uri-jira}IDA-${TICKET}[IDA-${TICKET}] "
fi


if [ ${TYPE} == "bugfix" ] || [ ${TYPE} == "Bugfix" ] || [ ${TYPE} == "bug" ] || [ ${TYPE} == "Bug" ]
then
   TYPE="bug"
   PREFIX="Bugfix : "
elif [ ${TYPE} == "feature" ] || [ ${TYPE} == "Feature" ] || [ ${TYPE} == "Feat" ] || [ ${TYPE} == "feat" ]
then
   TYPE="feature"
   PREFIX="Feature : "
elif [ ${TYPE} == "hotfix" ] || [ ${TYPE} == "Hotfix" ] || [ ${TYPE} == "Fix" ] || [ ${TYPE} == "fix" ]
then
   TYPE="hotfix"
   PREFIX="Hotfix : "
elif [ ${TYPE} == "version" ] || [ ${TYPE} == "Version" ] || [ ${TYPE} == "ugrade" ] || [ ${TYPE} == "Upgrade" ] || [ ${TYPE} == "upg" ]
then
   TYPE="improvement"
   PREFIX="Upgrade : "
elif [ ${TYPE} == "task" ] || [ ${TYPE} == "Task" ]
then
   TYPE="task"
   PREFIX="Task : "
else
   TYPE="undefined"
   PREFIX="Undefined : "
fi


#see change_release_version.sh tu get {uri-icon} and {uri-pr}
echo '''a| '`date +%d/%m/%y`' a| '${PREFIX}' '${ACT}'  a| image:{uri-icon}/'${TYPE}'.svg[] a| '${JIRA}' a| {uri-pr}/NPR[PR NPR]''' >> tmp;

tail -n+13  changelog.adoc >> tmp;

cat tmp > changelog.adoc;
rm tmp;

git add changelog.adoc
git commit -m "Insert changelog line"
git push origin ${BRANCH}
