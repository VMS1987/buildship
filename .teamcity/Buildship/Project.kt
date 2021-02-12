package Buildship

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger

object Project : Project({
    description = "Eclipse plugins for Gradle http://eclipse.org/buildship"

    vcsRoot(GitHubVcsRoot)

    template(EclipseBuildTemplate)

    params {
        param("env.JAVA_TOOL_OPTIONS", "-Dfile.encoding=UTF8")
        password("eclipse.downloadServer.password", "credentialsJSON:e600b1ef-46bb-4ced-b45b-795da6964956", label = "Password", display = ParameterDisplay.HIDDEN)
        password("eclipse.downloadServer.username", "credentialsJSON:23f3947f-45b2-46b8-83f6-9341c9b914f6", label = "Username", display = ParameterDisplay.HIDDEN)
        // Do not allow UI changes in the TeamCity configuration
        param("teamcity.ui.settings.readOnly", "true")
    }

    cleanup {
        all(days = 5)
        history(days = 5)
        artifacts(days = 5)
        preventDependencyCleanup = false
    }

    subProject(Buildship.Promotion30.Project)
    subProject(Buildship.Check30.Project)
    subProject(BuildshipExperimentalPipeline)
})

object BuildshipExperimentalPipeline : Project({
    name = "Experimental pipeline"
    subProject(BuildshipBuilds)
    subProject(BuildshipTriggers)
})

val ib1_1 = IndividualScenarioBuildType(type = "SanityCheck", os = "Linux", eclipseVersion = "4.13", javaVersion = "8")
val ib2_1 = IndividualScenarioBuildType(type = "Basic", os = "Linux", eclipseVersion = "4.3", javaVersion = "8")
val ib2_2 = IndividualScenarioBuildType(type = "Basic", os = "Linux", eclipseVersion = "4.12", javaVersion = "8")
val ib2_3 = IndividualScenarioBuildType(type = "Basic", os = "Windows", eclipseVersion = "4.3", javaVersion = "8")
val ib2_4 = IndividualScenarioBuildType(type = "Basic", os = "Windows", eclipseVersion = "4.6", javaVersion = "8")
val ib3_1 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.4", javaVersion = "8")
val ib3_2 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.5", javaVersion = "8")
val ib3_3 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.6", javaVersion = "8")
val ib3_4 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.7", javaVersion = "8")
val ib3_5 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.7", javaVersion = "9")
val ib3_6 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.8", javaVersion = "8")
val ib3_7 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.9", javaVersion = "8")
val ib3_8 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.10", javaVersion = "8")
val ib3_9 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.11", javaVersion = "8")
val ib3_10 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.12", javaVersion = "8")
val ib3_11 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.13", javaVersion = "8")
val ib3_12 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.14", javaVersion = "8")
val ib3_13 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.15", javaVersion = "8")
val ib3_14 = IndividualScenarioBuildType(type = "Full", os = "Linux", eclipseVersion = "4.16", javaVersion = "8")
val ib3_15 = IndividualScenarioBuildType(type = "Full", os = "Windows", eclipseVersion = "4.4", javaVersion = "8")
val ib3_16 = IndividualScenarioBuildType(type = "Full", os = "Windows", eclipseVersion = "4.7", javaVersion = "8")
val ib4_1 = IndividualScenarioBuildType(type = "CrossVersion", os = "Linux", eclipseVersion = "4.7", javaVersion = "9")

val individualBuildsForPhase1 = listOf(ib1_1)
val individualBuildsForPhase2 = listOf(ib2_1, ib2_2, ib2_3, ib2_4)
val individualBuildsForPhase3 = listOf(ib3_1, ib3_2, ib3_3, ib3_4, ib3_5, ib3_6, ib3_7, ib3_8, ib3_9, ib3_10, ib3_11,  ib3_12, ib3_13, ib3_14, ib3_15, ib3_16)
val individualBuildsForPhase4 = listOf(ib4_1)

val tb1_1 = TriggerBuildType("Sanity Check (Trigger)", individualBuildsForPhase1, null)
val tb1_2 = TriggerBuildType("Basic Test Coverage (Trigger, Phase 1/2)", individualBuildsForPhase1, null)
val tb2_2 = TriggerBuildType("Basic Test Coverage (Phase 2/2)", individualBuildsForPhase2, tb1_2)
val tb3_1 = TriggerBuildType("Full Test Coverage (Trigger, Phase 1/3)", individualBuildsForPhase1, null)
val tb3_2 = TriggerBuildType("Full Test Coverage (Phase 2/3)", individualBuildsForPhase2, tb3_1)
val tb3_3 = TriggerBuildType("Full Test Coverage (Phase 3/3)", individualBuildsForPhase3, tb3_2)
val tb4_1 = TriggerBuildType("Cross-Version Coverage (Trigger, Phase 1/4)", individualBuildsForPhase1, null)
val tb4_2 = TriggerBuildType("Cross-Version Test Coverage (Phase 2/4)", individualBuildsForPhase2, tb4_1)
val tb4_3 = TriggerBuildType("Cross-Version Test Coverage (Phase 3/4)", individualBuildsForPhase3, tb4_2)
val tb4_4 = TriggerBuildType("Cross-Version Test Coverage (Phase 4/4)", individualBuildsForPhase4, tb4_3)

object BuildshipBuilds : Project({
    name = "Individual coverage scenarios"
    id("Buildship_individual_coverage_scenarios")
    // TODO both EclipseBuildTemplate and the other template should be here
    buildTypesWithOrder(individualBuildsForPhase1 + individualBuildsForPhase2 + individualBuildsForPhase3 + individualBuildsForPhase4)
})

object BuildshipTriggers : Project({
    name = "Triggers"
    id("Buildship_triggers")
    buildTypesWithOrder(listOf(
        tb1_1,
        tb1_2, tb2_2,
        tb3_1, tb3_2, tb3_3,
        tb4_1, tb4_2, tb4_3, tb4_4))
})

class IndividualScenarioBuildType(type: String, os: String, eclipseVersion: String, javaVersion: String) : BuildType({
    createId("Individual", "${type}_Test_Coverage_${os}_Eclipse${eclipseVersion.replace(".", "_")}_Java${javaVersion}")

    templates(EclipseBuildTemplate)

    params {
        val sep = if (os == "Windows") "\\" else "/"
        param("eclipse.version", eclipseVersion.replace(".", ""))
        param("compiler.location", """%${os.toLowerCase()}.java${javaVersion}.oracle.64bit%${sep}bin${sep}javac""")
        param("eclipse.test.java.home", "%${os.toLowerCase()}.java${javaVersion}.oracle.64bit%")
        param("env.JAVA_HOME", "%${os.toLowerCase()}.java${javaVersion}.oracle.64bit%")
        param("enable.oomph.plugin", "false")
        when(type) {
            "Basic"        -> param("gradle.tasks", "clean eclipseTest")
            "Full"         -> param("gradle.tasks", "clean build")
            "SanityCheck"  -> param("gradle.tasks", "assemble checkstyleMain")
            "CrossVersion" -> param("gradle.tasks", "clean crossVersionEclipseTest")
            else -> throw RuntimeException("Unrecognized type: $type")
        }
    }

    requirements {
        contains("teamcity.agent.jvm.os.name", os)
    }
})

class TriggerBuildType(triggerName: String, scenarios: List<IndividualScenarioBuildType>, prev: TriggerBuildType?) : BuildType({
    createId("Trigger", triggerName)

    if (prev != null) {
        triggers {
            finishBuildTrigger {
                buildType = prev.id!!.value
                successfulOnly = true
                branchFilter = "+:*"
            }
        }

        dependencies {
             snapshot(prev, DefaultFailureCondition)
        }
    }


    dependencies {
        scenarios.forEach {
            snapshot(it, DefaultFailureCondition)
        }
    }
})

private fun Project.buildTypesWithOrder(buildTypes: List<BuildType>) {
    buildTypes.forEach { bt ->
        buildType(bt)
    }
    buildTypesOrder = buildTypes
}

private fun BuildType.createId(type: String, name: String)  {
    this.name = name
    id("${type}_${name.replace(" ", "_").replace("-", "_").replace("(", "").replace(")", "").replace("/", "").replace(",", "")}")
}

private val DefaultFailureCondition : SnapshotDependency.() -> Unit
    get() =  {
        onDependencyCancel = FailureAction.ADD_PROBLEM
        onDependencyFailure = FailureAction.FAIL_TO_START
    }