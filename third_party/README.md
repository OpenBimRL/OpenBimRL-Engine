# Third-party sources (Bazel)

`OpenBIMRL-API` and `BVH` are not on Maven Central. Bazel builds them from pinned
GitHub source archives declared in `MODULE.bazel` with Bzlmod
`use_repo_rule(..., "http_archive")` (not WORKSPACE-era `http_archive`):

| Target | Upstream | Pin |
|--------|----------|-----|
| `@openbimrl_api_src//:openbimrl_api` | [RUB-Informatik-im-Bauwesen/OpenBimRL](https://github.com/RUB-Informatik-im-Bauwesen/OpenBimRL) | `83bd65f52803d7e86a464b592899c6709888c47a` (same as `scripts/dev-start.sh`) |
| `@bvh_src//:bvh` | [RUB-Informatik-im-Bauwesen/Maven-Bounding-Volume-Hierarchy](https://github.com/RUB-Informatik-im-Bauwesen/Maven-Bounding-Volume-Hierarchy) | `d92129c5af88743e19b9ab801f69e3fb72baf46d` |

`nlohmann/json` is owned by the `openbimrl_native` module (`src/main/cpp/MODULE.bazel`),
not this directory.

Overlay BUILD files:

- `openbimrl_api.BUILD` — `java_library` + `jaxb-api` / `jaxb-runtime`
- `bvh.BUILD` — `java_library` + `j3d-core` / `vecmath`

OpenBimRL’s committed JAXB sources are ISO-8859-1; `MODULE.bazel` runs an
`iconv` `patch_cmds` step so JavaBuilder (UTF-8 default) can compile them.

Maven still installs API/BVH via `scripts/dev-start.sh` (`mvn install` from the same
repos). Do not commit `*.jar` under this directory; refresh pins by updating the
commit + `sha256` in `MODULE.bazel`.
