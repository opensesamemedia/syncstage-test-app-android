package media.opensesame.syncstagetestappandroid.model

import com.google.gson.annotations.SerializedName

data class GetSyncStageSecretResponse(

    @field:SerializedName("projectInfo")
    val projectInfo: ProjectInfo,

    @field:SerializedName("configurationVersion")
    val configurationVersion: Any,

    @field:SerializedName("issuedBy")
    val issuedBy: String,

    @field:SerializedName("applicationInfo")
    val applicationInfo: ApplicationInfo,

    @field:SerializedName("applicationSecret")
    val applicationSecret: ApplicationSecret
)

data class ApplicationInfo(

    @field:SerializedName("applicationUuid")
    val applicationUuid: String,

    @field:SerializedName("applicationPlatformUuid")
    val applicationPlatformUuid: String,

    @field:SerializedName("applicationPlatformName")
    val applicationPlatformName: String,

    @field:SerializedName("applicationName")
    val applicationName: String
)

data class ProjectInfo(

    @field:SerializedName("projectName")
    val projectName: String,

    @field:SerializedName("projectUuid")
    val projectUuid: String
)

data class ApplicationSecret(

    @field:SerializedName("applicationSecretId")
    val applicationSecretId: String,

    @field:SerializedName("applicationSecretKey")
    val applicationSecretKey: String
)
