# Distributed under the OSI-approved BSD 3-Clause License.  See accompanying
# file Copyright.txt or https://cmake.org/licensing for details.

cmake_minimum_required(VERSION 3.5)

file(MAKE_DIRECTORY
  "/home/remote/OpenBIMRL-Engine_workspace/OpenBimRL-Engine/src/main/cpp/cmake-build-debug/_deps/json-src"
  "/home/remote/OpenBIMRL-Engine_workspace/OpenBimRL-Engine/src/main/cpp/cmake-build-debug/_deps/json-build"
  "/home/remote/OpenBIMRL-Engine_workspace/OpenBimRL-Engine/src/main/cpp/cmake-build-debug/_deps/json-subbuild/json-populate-prefix"
  "/home/remote/OpenBIMRL-Engine_workspace/OpenBimRL-Engine/src/main/cpp/cmake-build-debug/_deps/json-subbuild/json-populate-prefix/tmp"
  "/home/remote/OpenBIMRL-Engine_workspace/OpenBimRL-Engine/src/main/cpp/cmake-build-debug/_deps/json-subbuild/json-populate-prefix/src/json-populate-stamp"
  "/home/remote/OpenBIMRL-Engine_workspace/OpenBimRL-Engine/src/main/cpp/cmake-build-debug/_deps/json-subbuild/json-populate-prefix/src"
  "/home/remote/OpenBIMRL-Engine_workspace/OpenBimRL-Engine/src/main/cpp/cmake-build-debug/_deps/json-subbuild/json-populate-prefix/src/json-populate-stamp"
)

set(configSubDirs )
foreach(subDir IN LISTS configSubDirs)
    file(MAKE_DIRECTORY "/home/remote/OpenBIMRL-Engine_workspace/OpenBimRL-Engine/src/main/cpp/cmake-build-debug/_deps/json-subbuild/json-populate-prefix/src/json-populate-stamp/${subDir}")
endforeach()
if(cfgdir)
  file(MAKE_DIRECTORY "/home/remote/OpenBIMRL-Engine_workspace/OpenBimRL-Engine/src/main/cpp/cmake-build-debug/_deps/json-subbuild/json-populate-prefix/src/json-populate-stamp${cfgdir}") # cfgdir has leading slash
endif()
