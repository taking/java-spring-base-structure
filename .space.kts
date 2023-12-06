// https://space.taking.kr/p/main/automation/jobs?repo=java-spring-base-structure&branch=refs%2Fheads%2Fmain&status=actual

job("Taking Space Automation") {  
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
    kotlinScript { api ->
      runIf("{{ isMainBranch }}")
      // if (api.gitBranch() == "refs/heads/main") {
        println("Build #" + api.executionNumber())
        
        api.gradlew("build")
        api.space().projects.planning.issues.createIssue()
      // }
    }
  }
}
