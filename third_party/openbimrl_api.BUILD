# Upstream JAXB-generated sources are ISO-8859-1; MODULE.bazel patch_cmds
# converts them to UTF-8 so JavaBuilder can compile under the default encoding.

load("@rules_java//java:defs.bzl", "java_library")

package(default_visibility = ["//visibility:public"])

java_library(
    name = "openbimrl_api",
    srcs = glob(["src/**/*.java"]),
    deps = [
        "@maven//:javax_xml_bind_jaxb_api",
        "@maven//:org_glassfish_jaxb_jaxb_runtime",
    ],
)
