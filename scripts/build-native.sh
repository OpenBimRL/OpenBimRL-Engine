#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${ROOT_DIR}"

skip_flag="${SKIP_NATIVE:-${skip_native:-false}}"
if [[ "${skip_flag}" == "true" ]]; then
  echo "Skipping native build (skip.native=true)."
  exit 0
fi

STAMP="${ROOT_DIR}/target/.native-sources-hash"
RESOURCE_LIB="${ROOT_DIR}/src/main/resources/lib.so"
NATIVE_LIB="${OPENBIMRL_NATIVE_CACHE_DIR:-${HOME}/.cache/openbimrl/native}/cmake/libOpenBIMRL_Native.so"

compute_hash() {
  {
    find "${ROOT_DIR}/src/main/cpp" -type f \( -name '*.cpp' -o -name '*.h' -o -name 'CMakeLists.txt' \) | sort
    echo "${ROOT_DIR}/Makefile"
  } | xargs sha256sum 2>/dev/null | sha256sum | awk '{print $1}'
}

current_hash="$(compute_hash)"

if [[ -f "${RESOURCE_LIB}" && -f "${STAMP}" && "$(cat "${STAMP}")" == "${current_hash}" ]]; then
  echo "Native library up to date, skipping build."
  exit 0
fi

make install

mkdir -p "${ROOT_DIR}/target"
echo "${current_hash}" > "${STAMP}"
