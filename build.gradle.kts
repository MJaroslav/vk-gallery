plugins {
    java
    application
    alias(libs.plugins.javafx)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.vk)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    
    compileOnly(libs.lombok)
    testCompileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    compileOnly(libs.annotations)
    testCompileOnly(libs.annotations)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}

application {
    mainClass.set("io.github.mjaroslav.vkgallery.VKGallery")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}
