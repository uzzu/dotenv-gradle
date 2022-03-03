plugins {
    base
    id("co.uzzu.dotenv.gradle") version "2.0.0"
    kotlin("jvm") version "1.4.31" apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("se.bjurr.violations.violation-comments-to-github-gradle-plugin") version "1.68.3"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

ktlint {
    sharedSettings()
}

tasks {
    sharedCommentPullRequestTask()
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    ktlint {
        sharedSettings()
    }

    apply(plugin = "se.bjurr.violations.violation-comments-to-github-gradle-plugin")
    tasks {
        sharedCommentPullRequestTask()
    }
}

fun org.jlleitschuh.gradle.ktlint.KtlintExtension.sharedSettings() {
    verbose.set(true)
    outputToConsole.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    ignoreFailures.set(true)
}

fun org.gradle.kotlin.dsl.TaskContainerScope.sharedCommentPullRequestTask() {
    if (env.GITHUB_EVENT_NAME.orNull() != "pull_request") {
        return
    }
    val commentToPullRequest by creating(se.bjurr.violations.comments.github.plugin.gradle.ViolationCommentsToGitHubTask::class) {
        setRepositoryOwner("uzzu")
        setRepositoryName("dotenv-gradle")
        setPullRequestId(env.PR_NUMBER.value)
        setoAuth2Token(env.GITHUB_TOKEN.value)
        setGitHubUrl("https://api.github.com")
        setCreateCommentWithAllSingleFileComments(false)
        setCreateSingleFileComments(true)
        setMinSeverity(se.bjurr.violations.lib.model.SEVERITY.WARN)
        setCommentTemplate(
            """
            **Reporter**: {{violation.reporter}}{{#violation.rule}}

            **Rule**: {{violation.rule}}{{/violation.rule}}
            **Severity**: {{violation.severity}}
            **File**: {{violation.file}} L{{violation.startLine}}{{#violation.source}}

            **Source**: {{violation.source}}{{/violation.source}}

            {{violation.message}}
            """.trimIndent()
        )
        setViolations(
            listOf(
                listOf("JUNIT", ".", ".*/build/test-results/.*/TEST-.*\\.xml\$", "JUnit"),
                listOf("CHECKSTYLE", ".", ".*/build/reports/ktlint/.*\\.xml\$", "ktlint"),
            )
        )
    }
}
