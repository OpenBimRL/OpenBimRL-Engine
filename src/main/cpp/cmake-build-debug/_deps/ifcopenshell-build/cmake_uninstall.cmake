################################################################################
#                                                                              #
# This file is part of IfcOpenShell.                                           #
#                                                                              #
# IfcOpenShell is free software: you can redistribute it and/or modify         #
# it under the terms of the Lesser GNU General Public License as published by  #
# the Free Software Foundation, either version 3.0 of the License, or          #
# (at your option) any later version.                                          #
#                                                                              #
# IfcOpenShell is distributed in the hope that it will be useful,              #
# but WITHOUT ANY WARRANTY; without even the implied warranty of               #
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the                 #
# Lesser GNU General Public License for more details.                          #
#                                                                              #
# You should have received a copy of the Lesser GNU General Public License     #
# along with this program. If not, see <http://www.gnu.org/licenses/>.         #
#                                                                              #
################################################################################

if(NOT EXISTS "/home/remote/OpenBIMRL-Engine_workspace/OpenBimRL-Engine/src/main/cpp/cmake-build-debug/install_manifest.txt")
    message(FATAL_ERROR "Cannot find install manifest: /home/remote/OpenBIMRL-Engine_workspace/OpenBimRL-Engine/src/main/cpp/cmake-build-debug/install_manifest.txt")
endif()

file(READ "/home/remote/OpenBIMRL-Engine_workspace/OpenBimRL-Engine/src/main/cpp/cmake-build-debug/install_manifest.txt" files)
string(REGEX REPLACE "\n" ";" files "${files}")

foreach(file ${files})
    message(STATUS "Uninstalling $ENV{DESTDIR}${file}")

    if(IS_SYMLINK "$ENV{DESTDIR}${file}" OR EXISTS "$ENV{DESTDIR}${file}")
        exec_program(
            "/home/remote/.cache/JetBrains/RemoteDev/dist/3d8b36b2566c3_CLion-241.14494.229/bin/cmake/linux/x64/bin/cmake" ARGS "-E remove \"$ENV{DESTDIR}${file}\""
            OUTPUT_VARIABLE rm_out
            RETURN_VALUE rm_retval
        )

        if(NOT "${rm_retval}" STREQUAL 0)
            message(FATAL_ERROR "Problem when removing $ENV{DESTDIR}${file}")
        endif()
    else(IS_SYMLINK "$ENV{DESTDIR}${file}" OR EXISTS "$ENV{DESTDIR}${file}")
        message(STATUS "File $ENV{DESTDIR}${file} does not exist.")
    endif()
endforeach()
