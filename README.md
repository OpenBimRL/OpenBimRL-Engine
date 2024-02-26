# OpenBimRL Engine

> **Current Version:** 2022.11.1 <br>
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
- [OpenBimRL-API](https://github.com/RUB-Informatik-im-Bauwesen/OpenBimRL)

More dependencies are provided in the _pom.xml_ and automatically included via maven.

### Minimum Example

An example execution of this engine is provided in _ConsoleApplication.kt_ and can be used to perform rule checking out of the
box. Results will be printed in the terminal. 