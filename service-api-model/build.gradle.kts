val versions = (extensions.getByName("versions") as Map<String, String>)

dependencies {
    compile(group = "com.fasterxml.jackson.core", name = "jackson-core", version = versions["jackson"])
    compile(group = "com.fasterxml.jackson.core", name = "jackson-annotations", version = versions["jackson"])
}