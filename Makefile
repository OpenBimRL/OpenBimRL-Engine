# Bazel is the primary Engine build. Legacy `make install` was removed.
#
#   bazel build //:native_lib_so   # or //:engine_lib / //:console
#   bazel test  //:engine_unit_tests //:engine_tests
#
# See BUILD.md. For a one-off CMake configure outside Bazel, use src/main/cpp/CMakeLists.txt
# with OPENBIMRL_USE_PREBUILT_IFCOPENSHELL=ON (see src/main/cpp/README.md).

.PHONY: all install build clean
all install build:
	@echo "Makefile targets removed — use Bazel (see BUILD.md):" >&2
	@echo "  bazel build //:engine_lib //:console" >&2
	@echo "  bazel test  //:engine_unit_tests //:engine_tests" >&2
	@exit 1

clean:
	@echo "Use: bazel clean  (and optionally rm -rf bazel-*)" >&2
	@exit 1
