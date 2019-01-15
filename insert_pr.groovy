
import utilities.LogRotator

def job = freeStyleJob('insert_pr') {
    label('common')
    description('this job insert a line on the changelog  by parsing the git branch on the create branch event')
    wrappers {
        colorizeOutput()
        preBuildCleanup()
        credentialsBinding {
            usernameColonPassword { //User register on Jenkins credential to use bitbucket
                variable('WHATEVER')
                credentialsId('whatever')
            }
        }
    }
    parameters {
        stringParam('PROJECT', '', 'Project Name')
        stringParam('GIT_REPOSITORY_NAME', '', 'repository name')
        stringParam('BRANCH', '', 'branche name')
        stringParam('HASH', '', 'hash of the parent commit')
        stringParam('CHANGE_TYPE', '', 'change type')
    }
    triggers {
        genericTrigger {
            genericVariables {
                genericVariable {
                    key("PROJECT")
                    value("\$.repository.project.key")
                    expressionType("JSONPath") //Optional, defaults to JSONPath
                    regexpFilter("") //Optional, defaults to empty string
                    defaultValue("") //Optional, defaults to empty string
                }
                genericVariable {
                    key("GIT_REPOSITORY_NAME")
                    value("\$.repository.name")
                    expressionType("JSONPath") //Optional, defaults to JSONPath
                    regexpFilter("") //Optional, defaults to empty string
                    defaultValue("") //Optional, defaults to empty string
                }
                genericVariable {
                    key("BRANCH")
                    value("\$.changes[0].ref.displayId")
                    expressionType("JSONPath") //Optional, defaults to JSONPath
                    regexpFilter("") //Optional, defaults to empty string
                    defaultValue("") //Optional, defaults to empty string
                }
                genericVariable {
                    key("HASH")
                    value("\$.changes.[0].fromHash")
                    expressionType("JSONPath") //Optional, defaults to JSONPath
                    regexpFilter("") //Optional, defaults to empty string
                    defaultValue("") //Optional, defaults to empty string
                }
            }
            token('secret_token')
            printContributedVariables(true)
            printPostContent(true)
            regexpFilterText('\$HASH')
            regexpFilterExpression('0000000000000000000000000000000000000000') //to check if it's a new branch
            causeString("remote_action")
        }
    }
    scm {
        git {
            remote {
                name('origin')
                url('ssh://git@repo_bitbucket.fr:port/${PROJECT}/${GIT_REPOSITORY_NAME}.git')
                credentials('whatever')
            }
            branch('master')
            extensions {
                relativeTargetDirectory('project')
            }
        }
    }
    steps {
        shell(readFileFromWorkspace('src/scripts/deploy/insert_pr.sh'))
    }
    publishers {
        wsCleanup {
            cleanWhenAborted(false)
            cleanWhenFailure(false)
            cleanWhenNotBuilt(false)
            cleanWhenUnstable(false)
            cleanWhenSuccess(true)
        }
    }
}

LogRotator.high(job)
