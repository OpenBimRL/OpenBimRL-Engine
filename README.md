# OpenBimRL Engine

> **Current Version:** 2023.07.1 <br>
> **Publication Date:** 01.11.2022 <br>
> **Updated Date:** 01.11.2022 <br>
> **Original Autors:** Marcel Stepien, André Vonthron <br>
> **Updated By:** Florian Becker <br>
> **E-Mail:** marcel.stepien@ruhr-uni-bochum.de ; florian.becker-f86@edu.ruhr-uni-bochum.de
<br>


This is a implementation of an engine for the OpenBimRL api. The framework includes functions for OpenBimRL parsing,
execution and generating results. This project also contains a set of already predefined functions that can be used in
OpenBimRL documents for defining precalculations and in extension perform rule checking.

## Getting started

### Requirements

**Dependencies:**
- [OpenBimRL-Schema](https://github.com/OpenBimRL/OpenBimRL-Schema) (`de.rub.bi.inf.openbimrl:schema`)
- [IFCOpenShell](https://github.com/IfcOpenShell/IfcOpenShell) (prebuilt at `/opt/ifcopenshell` in the DevContainer image)

**Build:** Bazel is primary — see [BUILD.md](BUILD.md) for `bazel build` / `bazel test` /
GitHub Packages publish / Docker (`llvm.dockerfile`, `rocm.dockerfile`, `nvcc.dockerfile`).
There is no Engine `pom.xml`. Maven consumers (e.g. Engine-REST) use the published
GitHub Packages artifact [`de.rub.bi.inf.openbimrl.engine:core`](https://github.com/OpenBimRL/OpenBimRL-Engine/packages/3211132).
JNI migration is a separate track.

### Minimum Example

An example execution of this engine is provided in _ConsoleApplication.kt_ and can be used to perform rule checking out of the
box. Results will be printed in the terminal.
