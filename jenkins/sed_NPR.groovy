
import utilities.LogRotator

def job = freeStyleJob('sed_NRP') {
    label('common')
    description('this job sed the id pr on the pr:opened event')
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
        stringParam('ACTOR_NAME', '', 'NNI case sensitive')
        stringParam('ID_NPR', '', 'Pr ID')
        stringParam('BRANCH', '', 'branche to sed')
    }
    triggers {
        genericTrigger {
            genericVariables {
                genericVariable {
                    key("PROJECT")
                    value("\$.pullRequest.fromRef.repository.project.key")
                    expressionType("JSONPath") //Optional, defaults to JSONPath
                    regexpFilter("") //Optional, defaults to empty string
                    defaultValue("") //Optional, defaults to empty string
                }
                genericVariable {
                    key("GIT_REPOSITORY_NAME")
                    value("\$.pullRequest.fromRef.repository.name")
                    expressionType("JSONPath") //Optional, defaults to JSONPath
                    regexpFilter("") //Optional, defaults to empty string
                    defaultValue("") //Optional, defaults to empty string
                }
                genericVariable {
                    key("ACTOR_NAME")
                    value("\$.actor.name")
                    expressionType("JSONPath") //Optional, defaults to JSONPath
                    regexpFilter("") //Optional, defaults to empty string
                    defaultValue("") //Optional, defaults to empty string
                }
                genericVariable {
                    key("ID_NPR")
                    value("\$.pullRequest.id")
                    expressionType("JSONPath") //Optional, defaults to JSONPath
                    regexpFilter("") //Optional, defaults to empty string
                    defaultValue("") //Optional, defaults to empty string
                }
                genericVariable {
                    key("BRANCH")
                    value("\$.pullRequest.fromRef.displayId")
                    expressionType("JSONPath") //Optional, defaults to JSONPath
                    regexpFilter("") //Optional, defaults to empty string
                    defaultValue("") //Optional, defaults to empty string
                }

            }
            token('secret_token')
            printContributedVariables(true)
            printPostContent(true)
            regexpFilterText("")
            regexpFilterExpression("")
        }
    }
steps {
    buildDescription('', '${PROJECT} ${GIT_REPOSITORY_NAME}')
}
    steps {
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
    }
    steps {
        shell(readFileFromWorkspace('src/scripts/deploy/pull_request_prepare.sh'))
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
