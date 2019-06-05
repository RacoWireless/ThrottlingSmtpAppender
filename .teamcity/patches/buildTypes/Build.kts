package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_1.*
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.NuGetInstallerStep
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.VisualStudioStep
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.nuGetInstaller
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.nuGetPack
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.visualStudio
import jetbrains.buildServer.configs.kotlin.v2018_1.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Build'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Build")) {
    expectSteps {
        nuGetInstaller {
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            projects = "source/Log4Net.ThrottlingSmtpAppender.sln"
            sources = """
                "https://www.nuget.org/api/v2/"
                "C:\NuGetPackages"
                %teamcity.nuget.feed.auth.server%
            """.trimIndent()
            param("nuget.updatePackages.mode", "sln")
            param("nugetCustomPath", "%teamcity.tool.NuGet.CommandLine.DEFAULT%")
            param("nugetPathSelector", "%teamcity.tool.NuGet.CommandLine.DEFAULT%")
        }
        visualStudio {
            path = "source/Log4Net.ThrottlingSmtpAppender.sln"
            version = VisualStudioStep.VisualStudioVersion.vs2013
            runPlatform = VisualStudioStep.Platform.x86
            msBuildVersion = VisualStudioStep.MSBuildVersion.V12_0
            msBuildToolsVersion = VisualStudioStep.MSBuildToolsVersion.V12_0
            configuration = "Release"
            param("octopus_octopack_package_version", "0.%sharedBuildNumber.id4%")
        }
        nuGetPack {
            name = "Package"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            paths = "source/Log4Net.ThrottlingSmtpAppender.csproj"
            version = "0.0.1.%teamcity.build.id%"
            outputDir = "Build/ackages"
            cleanOutputDir = true
            publishPackages = true
            includeSymbols = true
            param("nugetCustomPath", "%teamcity.tool.NuGet.CommandLine.DEFAULT%")
            param("nugetPathSelector", "%teamcity.tool.NuGet.CommandLine.DEFAULT%")
        }
    }
    steps {
        update<NuGetInstallerStep>(0) {
            sources = """
                "https://www.nuget.org/api/v2/"
                "C:\NuGetPackages"
                %teamcity.nuget.feed.httpAuth._Root.default.v2%
            """.trimIndent()
        }
    }
}
