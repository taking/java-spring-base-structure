// https://space.taking.kr/p/main/automation/jobs?repo=java-spring-base-structure&branch=refs%2Fheads%2Fmain&status=actual
job("Taking Space Automation :: Build and run tests") {
  container(displayName = "Gradle build", image = "eclipse-temurin:17.0.7_7-jdk") {
    kotlinScript { api ->
      if (api.gitBranch() == "refs/heads/main") {
        api.gradlew("build")
      }
    }
  }
}
