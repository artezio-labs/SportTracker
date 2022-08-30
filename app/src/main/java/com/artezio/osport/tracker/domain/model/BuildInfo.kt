package com.artezio.osport.tracker.domain.model

data class BuildInfo(
    val versionBuildId: String = "{{ VERSION_BUILD_ID }}",
    val versionBuildName: String = "{{ VERSION_BUILD_NAME }}",
)
