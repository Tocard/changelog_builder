
import utilities.LogRotator

def job = freeStyleJob('release') {
    label('common')
    description('this job create a release from a given project and update the new semver with the changelog')
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
        steps {
            shell(readFileFromWorkspace('src/scripts/deploy/change_release_version.sh'))
        }
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
