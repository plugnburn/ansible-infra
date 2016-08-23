def gitlabUrl="{{ gitlab_endpoint }}"

mavenJob("{{ project_name }}-master") {
  configure { node ->
    node/'properties'/'com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty' {
      gitLabConnection('GitLab Default')
    }
  }
  triggers {
    gitlabPush {
      buildOnMergeRequestEvents(false)
      buildOnPushEvents(true)
      enableCiSkip(false)
      setBuildDescription(false)
      addNoteOnMergeRequest(false)
      rebuildOpenMergeRequest('never')
      addVoteOnMergeRequest(false)
      useCiFeatures(false)
      includeBranches('master')
    }
  }
  scm {
    git {
      remote {
        url(gitlabUrl)
        credentials("{{ gitlab_private_key_id }}")
      }
      branch('master')
    }
  }
  preBuildSteps {
    maven {
      mavenInstallation('M3')
      goals('versions:set -DnewVersion=${BUILD_NUMBER}')
    }
  }
  mavenInstallation('M3')
  mavenOpts('-XX:MaxPermSize=128M -Dbuild.number=${BUILD_NUMBER}')
  goals('clean install')
  publishers {
    postBuildSteps {
      maven {
        mavenInstallation('M3')
        goals('-DDB_DRIVER=org.hsqldb.jdbcDriver -DDB_URL=jdbc:hsqldb:mem:petclinic -DDB_USERNAME=sa -DDB_PASSWORD= -DDB_TYPE=HSQL -DDB_SUBDIR=hsqldb -Ddocker.push.registry={{ docker_master_repo_url }} -Ddocker.push.username={{ docker_repo_user }} -Ddocker.push.password={{ docker_admin_password }} package docker:build docker:push')
      }
    }
  }
}

mavenJob("{{ project_name }}-pr") {
  configure { node ->
    node/'properties'/'com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty' {
      gitLabConnection('GitLab Default')
    }
  }
  triggers {
    gitlabPush {
      buildOnMergeRequestEvents(true)
      buildOnPushEvents(false)
      enableCiSkip(false)
      setBuildDescription(false)
      addNoteOnMergeRequest(false)
      rebuildOpenMergeRequest('never')
      addVoteOnMergeRequest(false)
      useCiFeatures(false)
    }
  }
  scm {
    git {
      remote {
        url(gitlabUrl)
        credentials("{{ gitlab_private_key_id }}")
      }
    }
  }
  mavenInstallation('M3')
  mavenOpts('-XX:MaxPermSize=128M -Dbuild.number=${BUILD_NUMBER}-SNAPSHOT -Dbuild.revision=${GIT_COMMIT}')
  goals('clean install')
  publishers {
    postBuildSteps {
      maven {
        mavenInstallation('M3')
        goals('-DDB_DRIVER=org.hsqldb.jdbcDriver -DDB_URL=jdbc:hsqldb:mem:petclinic -DDB_USERNAME=sa -DDB_PASSWORD= -DDB_TYPE=HSQL -Ddocker.push.registry={{ docker_pr_repo_url }} -Ddocker.push.username={{ docker_repo_user }} -Ddocker.push.password={{ docker_admin_password }} package docker:build docker:push')
      }
    }
  }
}