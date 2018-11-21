import jetbrains.buildServer.configs.kotlin.v2018_1.*
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.VisualStudioStep
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.visualStudio
import jetbrains.buildServer.configs.kotlin.v2018_1.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2018.1"

project {
    description = "Throttles Log4Net SMTP Messages"

    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.ON_SERVER
    }

    steps {
        step {
            type = "jb.nuget.installer"
            param("nuget.path", "%teamcity.tool.NuGet.CommandLine.DEFAULT%")
            param("nuget.sources", """
                "https://www.nuget.org/api/v2/"
                "C:\NuGetPackages"
                %teamcity.nuget.feed.auth.server%
            """.trimIndent())
            param("nuget.updatePackages.mode", "sln")
            param("sln.path", "source/Log4Net.ThrottlingSmtpAppender.sln")
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
        step {
            name = "Package"
            type = "jb.nuget.pack"
            param("nuget.pack.output.clean", "true")
            param("nuget.pack.specFile", "source/Log4Net.ThrottlingSmtpAppender.csproj")
            param("nuget.pack.include.sources", "true")
            param("nuget.pack.output.directory", "Build/ackages")
            param("nuget.path", "%teamcity.tool.NuGet.CommandLine.DEFAULT%")
            param("nuget.pack.as.artifact", "true")
            param("nugetCustomPath", "%teamcity.tool.NuGet.CommandLine.DEFAULT%")
            param("nuget.pack.version", "0.0.1.%teamcity.build.id%")
            param("nugetPathSelector", "%teamcity.tool.NuGet.CommandLine.DEFAULT%")
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        feature {
            type = "JetBrains.AssemblyInfo"
            param("assembly-format", "0.0.1")
        }
    }
})
