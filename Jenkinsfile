pipeline {
  agent any
  options {
    buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')
    quietPeriod 0
  }
  stages {
    stage('Build') {
      steps {
        sh 'set JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64'
        sh 'set GRADLE_USER_HOME=$WORKSPACE/.gradle'
        sh 'chmod +x gradlew'
        script {
          if (currentBuild.rawBuild.getCause(hudson.model.Cause$UpstreamCause) != null) {
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
            build job: 'GKMagic/main', wait: true
          }
        }
      }
      when { not { triggeredBy 'BuildUpstreamCause' } }
    }

    stage('Upload Artifacts') {
      steps {
        archiveArtifacts 'build/libs/*.jar'
      }
    }

    stage('Trigger Webhooks') {
      steps {
        webhookSend 'http://192.168.0.167:2813/api/v2/jenkins/webhook'
      }

      when { not { triggeredBy 'BuildUpstreamCause' } }
    }
  }
  post {
    always {
      script {
        if (currentBuild.rawBuild.getCause(hudson.model.Cause$UpstreamCause) == null) {
          discordSend description: '**Build:** ' + env.BUILD_ID + '\n**Branch:** ' + env.GIT_BRANCH + '\n**Status:** ' + currentBuild.currentResult + '\n', enableArtifactsList: true, footer: '', image: '', link: env.BUILD_URL, result: currentBuild.currentResult, scmWebUrl: 'https://github.com/GKPixelNew/GKProSkillAPI', showChangeset: true, thumbnail: '', title: env.JOB_NAME, webhookURL: 'https://canary.discord.com/api/webhooks/995605973999812618/qRPdkJW43SGFa9qsIhjTm3Gh4xj44LrKIm0aGGN7j2_DghX1asiQCOL_o9tlFXVXga0D'
        }
      }
    }
  }
}
