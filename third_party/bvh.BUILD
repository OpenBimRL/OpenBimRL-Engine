"""Build BVH from Maven-Bounding-Volume-Hierarchy sources."""

load("@rules_java//java:defs.bzl", "java_library")

package(default_visibility = ["//visibility:public"])

java_library(
    name = "bvh",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "@maven//:java3d_j3d_core",
        "@maven//:javax_vecmath_vecmath",
    ],
)
