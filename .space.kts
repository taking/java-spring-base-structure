// https://space.taking.kr/p/main/automation/jobs?repo=java-spring-base-structure&branch=refs%2Fheads%2Fmain&status=actual

job("Taking Space Automation :: Code Review-Build-Deploy") {  
  // To check a condition, basically, you need a kotlinScript step
  host(displayName = "Check branch") {
      kotlinScript { api ->
          // To pass the result of the condition to other steps, create a job parameter
          api.parameters["isMainBranch"] = (api.gitBranch() == "refs/heads/main").toString()
      }
  }

  container(displayName = "qodona Code Inspect", image = "jetbrains/qodana-jvm-community") {
      env["QODANA_TOKEN"] = Secrets("qodana-token")
      shellScript {
          content = """
              qodana
              """.trimIndent()
      }
  }

  container(displayName = "Build Step", image = "eclipse-temurin:17.0.7_7-jdk") {

    runIf("{{ isMainBranch }}")

    kotlinScript { api ->
        try {
            println("Build #" + api.executionNumber())
            api.gradlew("build")

            // dockerBuildPush {

            //     val spaceRepo = "mycompany.registry.jetbrains.space/p/prjkey/mydocker/myimage"
            //     tags {
            //         +"$spaceRepo:0.${"$"}JB_SPACE_EXECUTION_NUMBER"
            //         +"$spaceRepo:lts"
            //     }
            // }
        } catch (ex: Exception) {
            // get project Id
            val id = api.projectId()
            // get current build run number
            val runNumber = api.executionNumber()

            //get all issue statuses
            val statuses = api.space().projects.planning.issues.statuses.
            getAllIssueStatuses(project = ProjectIdentifier.Id(id))
            //get id of 'Open' issue status
            val openStatusId = statuses.find { it.name == "Open" }?.id
                ?: throw kotlin.Exception("The 'Open' state doesn't exist in the project")
            // create issue with 'Open' status
            api.space().projects.planning.issues.createIssue(
                project = ProjectIdentifier.Id(id),
                // generate name based on build run number
                title = "Job 'Build and publish' #$runNumber failed",
                description = "${ex.message}",
                status = openStatusId
            )
        }
    }


  }
}
