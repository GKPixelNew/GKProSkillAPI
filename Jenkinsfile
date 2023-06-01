pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'set JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64'
        sh 'set GRADLE_USER_HOME=$WORKSPACE/.gradle'
        sh 'chmod +x gradlew'
        script {
          if (currentBuild.getBuildCauses('hudson.model.Cause$UpstreamCause')) {
            sh './gradlew build -Dorg.gradle.java.home=/usr/lib/jvm/java-17-openjdk-amd64 --refresh-dependencies'
          } else {
            sh './gradlew build -Dorg.gradle.java.home=/usr/lib/jvm/java-17-openjdk-amd64'
          }
        }
      }
    }

    stage('Build Downstream') {
      failFast true
      parallel {
        stage('Build GKMagic') {
          steps {
            build job: 'GKMagic', wait: true
          }
        }
      }
      when {
        expression {
          return !currentBuild.getBuildCauses('hudson.model.Cause$UpstreamCause')
        }
      }
    }

    stage('Finalize') {
      steps {
        archiveArtifacts 'build/libs/*.jar'
        discordSend description: '**Build:** ' + env.BUILD_ID + '\n**Branch:** ' + env.GIT_BRANCH + '\n**Status:** ' + currentBuild.currentResult + '\n', enableArtifactsList: true, footer: '', image: '', link: env.BUILD_URL, result: currentBuild.currentResult, scmWebUrl: 'https://github.com/GKPixelNew/GKCore', showChangeset: true, thumbnail: '', title: env.JOB_NAME, webhookURL: 'https://canary.discord.com/api/webhooks/995605973999812618/qRPdkJW43SGFa9qsIhjTm3Gh4xj44LrKIm0aGGN7j2_DghX1asiQCOL_o9tlFXVXga0D'
        webhookSend 'https://eoj6oznymw2rd5r.m.pipedream.net'
      }

      when {
        expression {
          return !currentBuild.getBuildCauses('hudson.model.Cause$UpstreamCause')
        }
      }
    }
  }
}
