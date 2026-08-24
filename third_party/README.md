# Third-party sources (Bazel)

`OpenBimRL schema` and `BVH` are not on Maven Central. Bazel builds them from pinned
GitHub source archives declared in `MODULE.bazel` with Bzlmod
`use_repo_rule(..., "http_archive")` (not WORKSPACE-era `http_archive`):

| Target | Upstream | Pin |
|--------|----------|-----|
| `@openbimrl_api_src//:openbimrl_api` | [OpenBimRL/OpenBimRL-Schema](https://github.com/OpenBimRL/OpenBimRL-Schema) | `acc786261b48741a416691d53f6b64c1427e99a4` |
| `@bvh_src//:bvh` | [RUB-Informatik-im-Bauwesen/Maven-Bounding-Volume-Hierarchy](https://github.com/RUB-Informatik-im-Bauwesen/Maven-Bounding-Volume-Hierarchy) | `d92129c5af88743e19b9ab801f69e3fb72baf46d` |

`nlohmann/json` is owned by the `openbimrl_native` module (`src/main/cpp/MODULE.bazel`),
not this directory.

Overlay BUILD files:

- `openbimrl_api.BUILD` — `java_library` + `jaxb-api` / `jaxb-runtime`
- `bvh.BUILD` — `java_library` + `j3d-core` / `vecmath`

OpenBimRL’s committed JAXB sources are ISO-8859-1; `MODULE.bazel` runs an
`iconv` `patch_cmds` step so JavaBuilder (UTF-8 default) can compile them.

Do not commit `*.jar` under this directory; refresh pins by updating the
commit + `sha256` in `MODULE.bazel`.
